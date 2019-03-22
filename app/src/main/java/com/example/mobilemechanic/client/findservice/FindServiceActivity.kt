package com.example.mobilemechanic.client.findservice

import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.algolia.instantsearch.core.helpers.Searcher
import com.algolia.instantsearch.core.model.NumericRefinement
import com.algolia.instantsearch.ui.helpers.InstantSearch
import com.algolia.search.saas.AbstractQuery
import com.algolia.search.saas.Query
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_find_service.*
import kotlinx.android.synthetic.main.dialog_body_algolia_filter.*
import kotlinx.android.synthetic.main.dialog_container_basic.*


const val TWENTY_MI = 33000

class FindServiceActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mFireStore: FirebaseFirestore
    private lateinit var mAth: FirebaseAuth

    private lateinit var searcher: Searcher
    private lateinit var helper: InstantSearch
    private lateinit var priceRefinement: NumericRefinement
    private lateinit var distanceRefinement: NumericRefinement
    private lateinit var hits: HitsCustomized
    private var operatorLessThanOrEqual = NumericRefinement.OPERATOR_LE
    private var priceBelow = Double.MAX_VALUE
    private var spinnerSelectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_find_service)

        StrictMode.enableDefaults()

        mFireStore = FirebaseFirestore.getInstance()
        mAth = FirebaseAuth.getInstance()

        setUpFindServiceActivity()
    }

    private fun setUpFindServiceActivity() {
        setUpAlgolia()
        setUpToolBar()
        setUpFilterDialog()
    }

    private fun setUpAlgolia() {
        searcher = Searcher.create(
            getString(com.example.mobilemechanic.R.string.algolia_app_id),
            getString(com.example.mobilemechanic.R.string.algolia_api_key),
            getString(com.example.mobilemechanic.R.string.algolia_services_index)
        )

        val clientAddress = AddressManager.getUserAddress()
        val latlng = AddressManager.convertAddressToLatLng(this, clientAddress)
        val abstractQueryLatLng = AbstractQuery.LatLng(latlng.latitude, latlng.longitude)
        Log.d(CLIENT_TAG, "[FindServiceActivity] $latlng")
        searcher.query.aroundLatLng = abstractQueryLatLng
        searcher.query.aroundRadius = Query.RADIUS_ALL
        searcher.query.getRankingInfo = true

        helper = InstantSearch(this, searcher)
        helper.search()

        hits = findViewById(com.example.mobilemechanic.R.id.id_hits_customized)
        hits.enableKeyboardAutoHiding()

//        searchBox.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onQueryTextChange(query: String?): Boolean {
//                Log.d(CLIENT_TAG, "[FindServiceActivity] onQueryTextChange $query")
//                searcher.search(query)
//                return true
//            }
//
//        })
    }


    private fun setUpToolBar() {
        val arrow = resources.getDrawable(com.example.mobilemechanic.R.drawable.abc_ic_ab_back_material, null)
        arrow.setColorFilter(
            resources.getColor(com.example.mobilemechanic.R.color.colorPrimary),
            PorterDuff.Mode.SRC_ATOP
        )
        setSupportActionBar(id_find_service_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(arrow)
        }
    }

    private fun setUpFilterDialog() {
        id_filter.setOnClickListener {
            val container = layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)
            val body = layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_body_algolia_filter, null)
            val basicDialog = BasicDialog.Builder.apply {
                title = "Filter"
                positive = "Save"
                negative = "Cancel"
            }.build(this, container, body)
            basicDialog.show()

            setUpDialogSpinner(basicDialog)
            handleDialogOnClick(basicDialog)

        }
    }

    private fun setUpDialogSpinner(basicDialog: Dialog) {
        basicDialog.id_algolia_filter_price_spinner.adapter = HintSpinnerAdapter(
            this,
            com.example.mobilemechanic.R.layout.support_simple_spinner_dropdown_item,
            DataProviderManager.getServicePriceLabel(),
            "Price"
        )

        basicDialog.id_algolia_filter_price_spinner.onItemSelectedListener = this
        basicDialog.id_algolia_filter_price_spinner.setSelection(spinnerSelectedPosition)
    }

    private fun handleDialogOnClick(basicDialog: Dialog) {
        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }

        basicDialog.id_positive.setOnClickListener {
            filterPrice()
            basicDialog.dismiss()
        }
    }

    private fun filterPrice() {
        searcher.query.setFilters("service.price<=$priceBelow")
        searcher.search()
//        priceRefinement = NumericRefinement("service.price", operatorLessThanOrEqual, priceBelow)
//        searcher.addNumericRefinement(priceRefinement)
//        searcher.search()
    }

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedValue = DataProviderManager.getServicePriceValue()[position]
        priceBelow = selectedValue
        spinnerSelectedPosition = position
        Log.d(CLIENT_TAG, "[FindServiceActivity] price spinner position: $position")
        Log.d(CLIENT_TAG, "[FindServiceActivity] selectedValue price: $selectedValue")
    }
}
