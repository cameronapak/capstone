package com.example.mobilemechanic.client.postserviceissue

import android.R
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.activity_post_service_request.*


class PostServiceRequestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_post_service_request)
        setUpPostServiceRequestActivity()
    }

    private fun setUpPostServiceRequestActivity() {
        setUpToolBar()
        setUpFormSpinners()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_service_form_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpFormSpinners() {
        setUpVehicleSpinner()
    }

    private fun setUpVehicleSpinner() {
        val vehicles =
            arrayOf("Vehicle","2011 Toyota Venza", "2013 Toyota Camry")
                .asList()

        if (isGarageEmpty(vehicles)) {
            showWarningIconAndMessage()
            id_submit.isEnabled = false
        } else {
            hideWarningIconAndMessage()
            id_vehicle_spinner.adapter =
                HintSpinnerAdapter(
                    this,
                    R.layout.simple_spinner_dropdown_item,
                    vehicles
                )
        }
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun hideWarningIconAndMessage() {
        id_warning_icon.visibility = View.GONE
        id_warningMessage.visibility = View.GONE
        id_warningMessageAddVehicle.visibility = View.GONE
    }

    private fun showWarningIconAndMessage() {
        id_warning_icon.visibility = View.VISIBLE
        id_warningMessage.visibility = View.VISIBLE
        id_warningMessageAddVehicle.visibility = View.VISIBLE
    }

    private fun isGarageEmpty(vehicles: List<String>): Boolean {
        if (vehicles.size <= 1) {
            return true
        }
        return false
    }
}