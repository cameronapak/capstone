package com.example.mobilemechanic.client.findservice

import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.algolia.instantsearch.core.helpers.Searcher
import com.algolia.instantsearch.core.model.NumericRefinement
import com.algolia.instantsearch.ui.helpers.InstantSearch
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_find_service.*
import kotlinx.android.synthetic.main.dialog_body_algolia_filter.*
import kotlinx.android.synthetic.main.dialog_container_basic.*


class FindServiceActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mFireStore: FirebaseStorage
    private lateinit var searcher: Searcher
    private lateinit var index: Index
    private lateinit var client: Client
    private lateinit var helper: InstantSearch
    private var priceRefinement: NumericRefinement? = null
    private var ratingRefinement: NumericRefinement? = null
    private var operatorLessThanOrEqual = NumericRefinement.OPERATOR_LE
    private var priceBelow = Double.MAX_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_find_service)
        mFireStore = FirebaseStorage.getInstance()
        setUpFindServiceActivity()
    }

    private fun setUpFindServiceActivity() {
        setUpAlgolia()
        setUpToolBar()
        setUpFilterDialog()
    }

    private fun setUpAlgolia() {
        searcher = Searcher.create(getString(com.example.mobilemechanic.R.string.algolia_app_id),
            getString(com.example.mobilemechanic.R.string.algolia_api_key),
            getString(com.example.mobilemechanic.R.string.algolia_services_index))

        helper = InstantSearch(this, searcher)
        helper.search()
        val hits = findViewById<HitsCustomized>(com.example.mobilemechanic.R.id.id_hits_customized)
        hits.enableKeyboardAutoHiding()
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
            DataProviderManager.getServicePriceLabel()
        )

        basicDialog.id_algolia_filter_price_spinner.onItemSelectedListener = this
    }

    private fun handleDialogOnClick(basicDialog: Dialog) {
        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }

        basicDialog.id_positive.setOnClickListener {

            checkRating(basicDialog)
            checkPrice()
            basicDialog.dismiss()
        }
    }

    private fun checkRating(basicDialog: Dialog) {
        Log.d(CLIENT_TAG, "[FindServiceActivity] rating ${basicDialog.id_algolia_filter_rating.isChecked}")
//        if (basicDialog.id_algolia_filter_rating.isChecked) {
//            Log.d(CLIENT_TAG, "[FindServiceActivity] rank by rating")
//            searcher.removeNumericRefinement("service.price")
//            ratingRefinement = NumericRefinement("mechanicInfo.rating", NumericRefinement.OPERATOR_GT, 1.0)
//
//            searcher.addNumericRefinement(ratingRefinement as NumericRefinement)
//            searcher.search()
//        } else {
//            searcher.removeNumericRefinement("mechanicInfo.rating")
//            searcher.search()
//        }
    }

    private fun checkPrice() {
        priceRefinement = NumericRefinement("service.price", operatorLessThanOrEqual, priceBelow)
        searcher.addNumericRefinement(priceRefinement as NumericRefinement)
        searcher.search()
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
        Log.d(CLIENT_TAG, "[FindServiceActivity] price spinner position: $position")
        Log.d(CLIENT_TAG, "[FindServiceActivity] selectedValue price: $selectedValue")
//        priceRefinement = NumericRefinement("service.price", operatorLessThanOrEqual, selectedValue)
//        searcher.addNumericRefinement(priceRefinement as NumericRefinement)
//        searcher.search()
    }
}
