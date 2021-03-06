package com.example.mobilemechanic.client.findservice

import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.AdapterView
import com.algolia.instantsearch.core.helpers.Searcher
import com.algolia.instantsearch.core.model.NumericRefinement
import com.algolia.instantsearch.examples.icebnb.widgets.MapWidget
import com.algolia.instantsearch.ui.helpers.InstantSearch
import com.algolia.search.saas.AbstractQuery
import com.algolia.search.saas.Query
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.AuthenticationManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_find_service.*
import kotlinx.android.synthetic.main.dialog_body_algolia_filter.*
import kotlinx.android.synthetic.main.dialog_container_basic.*





class FindServiceActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mFireStore: FirebaseFirestore
    private lateinit var mAth: FirebaseAuth

    private lateinit var searcher: Searcher
    private lateinit var helper: InstantSearch
    private lateinit var priceRefinement: NumericRefinement
    private lateinit var hits: HitsCustomized
    private lateinit var mapWidget: MapWidget
    private lateinit var mapFragment: SupportMapFragment
    private var isMapToggleOn = false

    private var operatorLessThanOrEqual = NumericRefinement.OPERATOR_LE
    private var priceBelow = Double.MAX_VALUE
    private var spinnerSelectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_find_service)
        mFireStore = FirebaseFirestore.getInstance()
        mAth = FirebaseAuth.getInstance()
        setUpFindServiceActivity()
    }

    private fun setUpFindServiceActivity() {
        setUpAlgolia()
        setUpToolBar()
        setUpFilterDialog()
        enableMapToggle()
    }

    private fun setUpAlgolia() {
        searcher = Searcher.create(
            getString(com.example.mobilemechanic.R.string.algolia_app_id),
            getString(com.example.mobilemechanic.R.string.algolia_api_key),
            getString(com.example.mobilemechanic.R.string.algolia_services_index)
        )

        enableGeoRanking(searcher)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragmentTransaction.hide(mapFragment)
        fragmentTransaction.commit()

        mapWidget = MapWidget(this, mapFragment)
        searcher.registerResultListener(mapWidget)

        helper = InstantSearch(this, searcher)
        helper.search()
        hits = findViewById(com.example.mobilemechanic.R.id.id_hits_customized)
        hits.enableKeyboardAutoHiding()
    }

    private fun enableGeoRanking(searcher: Searcher) {
        if (AddressManager.hasAddress()) {
            val clientAddress = AddressManager.getUserAddress()
            val abstractQueryLatLng =
                AbstractQuery.LatLng(clientAddress!!._geoloc.lat, clientAddress!!._geoloc.lng)
            searcher.query.aroundLatLng = abstractQueryLatLng
            searcher.query.aroundRadius = Query.RADIUS_ALL
            searcher.query.getRankingInfo = true
        }
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

    private fun enableMapToggle() {
        id_map_toggle.setOnClickListener {
            val ft = supportFragmentManager.beginTransaction()
            isMapToggleOn = if (isMapToggleOn) {
                ft.hide(mapFragment).commit()
                id_map_toggle.text = "Map"
                false
            } else {
                ft.show(mapFragment).commit()
                id_map_toggle.text = "List"
                true
            }
        }
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
        priceRefinement = NumericRefinement("service.price", operatorLessThanOrEqual, priceBelow)
        searcher.addNumericRefinement(priceRefinement)
        searcher.search()
    }

    private fun enableHideKeyboard() {
        id_find_service_layout.setOnClickListener {
            ScreenManager.hideKeyBoard(this)
        }

        id_find_service_toolbar.setOnClickListener {
            ScreenManager.hideKeyBoard(this)
        }
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
    }

    override fun onResume() {
        AuthenticationManager.signInGuard(this)
        enableHideKeyboard()
        super.onResume()
    }
}
