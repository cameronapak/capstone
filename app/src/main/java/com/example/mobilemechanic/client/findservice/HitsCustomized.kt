package com.example.mobilemechanic.client.findservice

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.algolia.instantsearch.R
import com.algolia.instantsearch.core.events.ResetEvent
import com.algolia.instantsearch.core.helpers.Highlighter
import com.algolia.instantsearch.core.helpers.Searcher
import com.algolia.instantsearch.core.model.*
import com.algolia.instantsearch.core.utils.JSONUtils
import com.algolia.instantsearch.ui.databinding.BindingHelper
import com.algolia.instantsearch.ui.databinding.RenderingHelper
import com.algolia.instantsearch.ui.utils.LayoutViews
import com.algolia.instantsearch.ui.views.AlgoliaHitView
import com.algolia.search.saas.AlgoliaException
import com.algolia.search.saas.Query
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.postservicerequest.PostServiceRequestActivity
import com.example.mobilemechanic.client.review.ReviewActivity
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.example.mobilemechanic.model.dto.Address
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.*

const val EXTRA_SERVICE = "extra_service"
const val EXTRA_MECHANIC_INFO = "extra_mechanic_info"

class HitsCustomized
    (context: Context, attrs: AttributeSet) : RecyclerView(context, attrs), AlgoliaResultsListener,
    AlgoliaErrorListener, AlgoliaSearcherListener {

    private var remainingItemsBeforeLoading: Int
    var layoutId: Int = 0
        protected set

    var adapter: HitsAdapter
    lateinit var searcher: Searcher
    private var imeManager: InputMethodManager
    private var gson: Gson
    private var infiniteScrollListener: InfiniteScrollListener?
    private var keyboardListener: RecyclerView.OnScrollListener? = null
    private var emptyView: View? = null

    init {

        if (isInEditMode) {
            remainingItemsBeforeLoading = 0
            layoutId = 0
            infiniteScrollListener = null
            this.layoutManager = null
        }

        gson = Gson()

        val infiniteScroll: Boolean
        val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.Hits, 0, 0)
        try {
            layoutId = styledAttributes.getResourceId(R.styleable.Hits_itemLayout, 0)
            infiniteScroll = styledAttributes.getBoolean(R.styleable.Hits_infiniteScroll, true)
            if (styledAttributes.getBoolean(R.styleable.Hits_autoHideKeyboard, false)) {
                enableKeyboardAutoHiding()
            }

            val remainingItemsAttribute =
                styledAttributes.getInt(R.styleable.Hits_remainingItemsBeforeLoading, MISSING_VALUE)
            if (remainingItemsAttribute == MISSING_VALUE) {
                remainingItemsBeforeLoading = DEFAULT_REMAINING_ITEMS
            } else {
                remainingItemsBeforeLoading = remainingItemsAttribute
                if (!infiniteScroll) {
                    throw IllegalStateException(Errors.HITS_INFINITESCROLL_BUT_REMAINING)
                }
            }

            BindingHelper.setVariantForView(this, attrs)
        } finally {
            styledAttributes.recycle()
        }

        this.setHasFixedSize(true) // Enables optimisations as the view's width & height are fixed

        adapter = HitsAdapter()
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                updateEmptyView()
            }
        })
        setAdapter(adapter)

        imeManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        layoutManager = LinearLayoutManager(context)

        infiniteScrollListener = if (infiniteScroll) InfiniteScrollListener() else null
        if (infiniteScroll) {
            addOnScrollListener(infiniteScrollListener!!)
        }
    }

    private fun clear() {
        adapter.clear()
    }

    operator fun get(position: Int): JSONObject {
        return adapter.getItemAt(position)
    }

    fun enableKeyboardAutoHiding() {
        keyboardListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dx != 0 || dy != 0) {
                    imeManager.hideSoftInputFromWindow(
                        this@HitsCustomized.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        }
        addOnScrollListener(keyboardListener!!)

    }

    fun disableKeyboardAutoHiding() {
        removeOnScrollListener(keyboardListener!!)
        keyboardListener = null
    }

    protected fun addHits(results: SearchResults?, isReplacing: Boolean) {
        if (results == null) {
            if (isReplacing) {
                clear()
                if (infiniteScrollListener != null) {
                    infiniteScrollListener!!.setCurrentlyLoading(false)
                }
            }
            return
        }

        val hits = results.hits

        if (isReplacing) {
            adapter.clear(false)
        }

        for (i in 0 until hits.length()) {
            val hit = hits.optJSONObject(i)
            if (hit != null) {
                adapter.add(hit)
            }
        }

        if (isReplacing) {
            adapter.notifyDataSetChanged()
            smoothScrollToPosition(0)
            if (infiniteScrollListener != null) {
                infiniteScrollListener!!.setCurrentlyLoading(false)
            }
        } else {
            adapter.notifyItemRangeInserted(adapter.itemCount, hits.length())
        }
    }

    protected fun getFinalAttributeValue(
        hit: JSONObject,
        view: View,
        attribute: String,
        attributeValue: String?
    ): Spannable? {
        var attributeText: Spannable? = null
        if (attributeValue != null) {
            if (RenderingHelper.getDefault().shouldHighlight(view, attribute)) {
                val highlightColor = RenderingHelper.getDefault().getHighlightColor(view, attribute)!!
                val prefix = BindingHelper.getPrefix(view)
                val suffix = BindingHelper.getSuffix(view)
                val snippetted = RenderingHelper.getDefault().shouldSnippet(view, attribute)
                attributeText = Highlighter.getDefault().setInput(hit, attribute, prefix, suffix, false, snippetted)
                    .setStyle(highlightColor).render()
            } else {
                attributeText = SpannableString(attributeValue)
            }
        }
        return attributeText
    }

    private fun updateEmptyView() {
        if (emptyView == null) {
            return
        }
        if (adapter.itemCount == 0) {
            emptyView!!.visibility = View.VISIBLE
        } else {
            emptyView!!.visibility = View.GONE
        }
    }

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBus.getDefault().register(this)
    }

    public override fun onDetachedFromWindow() {
        EventBus.getDefault().unregister(this)
        super.onDetachedFromWindow()
    }

    override fun initWithSearcher(searcher: Searcher) {
        Log.d(CLIENT_TAG, "initWithSearcher")
        this.searcher = searcher
    }

    override fun onResults(results: SearchResults, isLoadingMore: Boolean) {
        Log.d(CLIENT_TAG, "[HitsCustomized] result $results")
        Log.d(CLIENT_TAG, "[HitsCustomized] ${results.content}")
        addHits(results, !isLoadingMore)
    }

    override fun onError(query: Query, error: AlgoliaException) {
        Log.e("Algolia|Hits", "Error while searching '" + query.query + "':" + error.message)
    }

    @Subscribe
    fun onReset(event: ResetEvent) {
        addHits(null, true)
    }

    fun setEmptyView(emptyView: View?) {
        this.emptyView = emptyView
    }

    inner class InfiniteScrollListener : RecyclerView.OnScrollListener() {
        private var lastItemCount = 0 // Item count after last event
        private var currentlyLoading = true // Are we waiting for new results?

        private// last position = biggest value within the list of positions
        val lastVisibleItemPosition: Int
            get() {
                var lastVisiblePosition = 0
                if (layoutManager is StaggeredGridLayoutManager) {
                    val lastVisibleItemPositions =
                        (layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                    var maxSize = lastVisibleItemPositions[0]
                    for (lastVisibleItemPosition in lastVisibleItemPositions) {
                        if (lastVisibleItemPosition > maxSize) {
                            maxSize = lastVisibleItemPosition
                        }
                    }
                    lastVisiblePosition = maxSize
                } else if (layoutManager is LinearLayoutManager) {
                    lastVisiblePosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                }
                return lastVisiblePosition
            }

        internal fun setCurrentlyLoading(currentlyLoading: Boolean) {
            this.currentlyLoading = currentlyLoading
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!searcher.hasMoreHits()) {
                return
            }

            val totalItemCount = layoutManager!!.itemCount
            if (totalItemCount < lastItemCount) {
                // we have less elements than before, the count should be reset
                lastItemCount = totalItemCount
                if (totalItemCount == 0) {
                    // the list is empty -> do nothing until we get more results.
                    setCurrentlyLoading(true)
                    return
                }
            }

            if (currentlyLoading) {
                if (totalItemCount > lastItemCount) {
                    // the data changed, loading is finished
                    setCurrentlyLoading(false)
                    lastItemCount = totalItemCount
                }
            } else {
                val lastVisiblePosition = lastVisibleItemPosition

                if (lastVisiblePosition + remainingItemsBeforeLoading > totalItemCount) {
                    // we are under the loading threshold, let's load more data
                    searcher.loadMore()
                    setCurrentlyLoading(true)
                }
            }
        }
    }

    inner class HitsAdapter internal constructor() : RecyclerView.Adapter<HitsAdapter.ViewHolder>() {

        private var hits = ArrayList<JSONObject>()
        private val placeholders = SparseArray<Drawable>()
        private lateinit var clientAddress: Address

        init {
            this.hits = ArrayList()
        }

        @JvmOverloads
        internal fun clear(shouldNotify: Boolean = true) {
            if (shouldNotify) {
                val previousItemCount = itemCount
                hits.clear()
                notifyItemRangeRemoved(0, previousItemCount)
            } else {
                hits.clear()
            }
        }

        fun add(result: JSONObject) {
            hits.add(result)
        }

        internal fun getItemAt(position: Int): JSONObject {
            return hits[position]
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context), layoutId, parent, false
            )
            binding.executePendingBindings()
            return ViewHolder(binding.root)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val serviceJson = hits[position]
            Log.d(CLIENT_TAG, "[HitsCustomized] $serviceJson")
            val serviceObj = gson.fromJson(serviceJson.toString(), ServiceModel::class.java)

            holder.price.text = "$${serviceObj.service.price.toInt()}"
            holder.mechanicName.text =
                "${serviceObj.mechanicInfo.basicInfo.firstName} ${serviceObj.mechanicInfo.basicInfo.lastName}"
            holder.selectButton.setOnClickListener {
                val intent = Intent(context, PostServiceRequestActivity::class.java)
                intent.putExtra(EXTRA_SERVICE, serviceObj)
                context.startActivity(intent)
            }

            holder.hitItem.setOnClickListener {
                ScreenManager.hideKeyBoard(context, it)
            }

            holder.mechanicRating.rating = serviceObj.mechanicInfo.rating

            if (AddressManager.hasAddress()) {
                val userAddress = AddressManager.getUserAddress()
                Log.d(CLIENT_TAG, "[HitsCustomized] $userAddress")
                val distance = AddressManager.getDistanceMI(userAddress!!._geoloc, serviceObj._geoloc)
                holder.distance.text = context.getString(com.example.mobilemechanic.R.string.miles, distance)
            }

            holder.review.setOnClickListener {
                val intent = Intent(context, ReviewActivity::class.java)
                intent.putExtra(EXTRA_MECHANIC_INFO, serviceObj.mechanicInfo)
                context.startActivity(intent)
            }

            val mappedViews = holder.viewMap.keys
            val hitViews =
                LayoutViews.findByClass(holder.itemView as ViewGroup, AlgoliaHitView::class.java)
            val hit = hits[position]

            for (hitView in hitViews) {
                if (mappedViews.contains(hitView as View)) {
                    continue
                }
                hitView.onUpdateView(hit)
            }

            // For every view we have bound, if we can handle its class let's send them the hit
            for ((view, attributeName) in holder.viewMap) {
                if (attributeName == null) {
                    Log.d("Hits", "View $view is bound to null attribute, skipping")
                    continue
                }
                val attributeValue = JSONUtils.getStringFromJSONPath(hit, attributeName)
                if (attributeValue == null) {
                    Log.d("Hits", "View $view is bound to attribute $attributeName missing in JSON, skipping")
                    continue
                }
                val fullAttribute = BindingHelper.getFullAttribute(view, attributeValue)
                if (view is AlgoliaHitView) {
                    (view as AlgoliaHitView).onUpdateView(hit)
                } else if (view is RatingBar) {
                    view.rating = getFloatValue(fullAttribute)
                } else if (view is ProgressBar) {
                    view.progress = Math.round(getFloatValue(fullAttribute))
                } else if (view is EditText) {
                    view.hint = getFinalAttributeValue(hit, view, attributeName, fullAttribute)
                } else if (view is TextView) {
                    view.text = getFinalAttributeValue(hit, view, attributeName, fullAttribute)
                } else if (view is ImageView) {
                    val activity = getActivity(view)
                    if (activity == null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) {
                        continue
                    }

                    val viewId = view.id
                    var placeholder: Drawable? = placeholders.get(viewId)
                    if (placeholder == null) {
                        placeholder = view.drawable
                        placeholders.put(viewId, placeholder)
                    }
                    val requestOptions = RequestOptions().placeholder(placeholder)
                    when (view.scaleType) {
                        ImageView.ScaleType.CENTER_CROP -> requestOptions.centerCrop()
                        ImageView.ScaleType.FIT_CENTER -> requestOptions.fitCenter()
                        ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_END, ImageView.ScaleType.MATRIX, ImageView.ScaleType.CENTER -> {
                        }
                        ImageView.ScaleType.CENTER_INSIDE -> requestOptions.centerInside()
                        else -> requestOptions.centerInside()
                    }
                    Glide.with(activity).applyDefaultRequestOptions(requestOptions).load(fullAttribute).into(view)
                } else {
                    throw IllegalStateException(
                        String.format(
                            Errors.ADAPTER_UNKNOWN_VIEW,
                            view.javaClass.canonicalName
                        )
                    )
                }
            }
        }

        private fun getFloatValue(attributeValue: String?): Float {
            return if (attributeValue != null) java.lang.Float.parseFloat(attributeValue) else 0f
        }

        override fun getItemCount(): Int {
            return hits.size
        }

        private fun getActivity(view: View): Activity? {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
            Log.e("Algolia|Hits", "Error: Could not get activity from View.")
            return null
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val viewMap = HashMap<View, String>()
            private val defaultValue = "ADefaultValueForHitsVariant"
            val mechanicName = itemView.findViewById<TextView>(com.example.mobilemechanic.R.id.id_mechanic_name)
            val selectButton = itemView.findViewById<Button>(com.example.mobilemechanic.R.id.id_select)
            val price = itemView.findViewById<TextView>(com.example.mobilemechanic.R.id.id_price)
            val hitItem = itemView.findViewById<ConstraintLayout>(com.example.mobilemechanic.R.id.id_algolia_hit_item)
            val mechanicRating = itemView.findViewById<RatingBar>(com.example.mobilemechanic.R.id.id_mechanic_rating)
            val distance = itemView.findViewById<TextView>(com.example.mobilemechanic.R.id.id_distance)
            val review = itemView.findViewById<TextView>(com.example.mobilemechanic.R.id.id_review)

            init {
                var indexVariant: String? = defaultValue
                val views = LayoutViews.findAny(itemView as ViewGroup)
                for (view in views) {
                    if (!BindingHelper.isBound(view.id)) {
                        continue // If the view is not bound, we can skip it
                    }
                    if (view is AlgoliaHitView) {
                        continue
                    }

                    val viewIndexVariant = BindingHelper.getVariantForView(view)
                    val isSameVariant =
                        if (viewIndexVariant == null) indexVariant == null else viewIndexVariant == indexVariant
                    if (defaultValue != indexVariant && !isSameVariant) {
                        throw IllegalStateException("Hits found two conflicting variants within its views: $indexVariant / $viewIndexVariant.")
                    }
                    indexVariant = viewIndexVariant
                }

                val attributes = BindingHelper.getBindings(indexVariant)
                if (attributes != null) { // Ensure we have at least some bindings
                    for ((key, value) in attributes) {
                        if (value != null) { // attribute can be null e.g. if view is a Hits
                            viewMap[itemView.findViewById(key)] = value
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val DEFAULT_REMAINING_ITEMS = 5
        private const val MISSING_VALUE = Integer.MIN_VALUE
    }
}