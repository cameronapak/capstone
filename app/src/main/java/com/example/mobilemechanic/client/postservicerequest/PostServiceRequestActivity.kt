package com.example.mobilemechanic.client.postservicerequest

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.activity_post_service_request.*
import kotlinx.android.synthetic.main.basic_dialog.view.*
import kotlinx.android.synthetic.main.dialog_body_availability.view.*


class PostServiceRequestActivity : AppCompatActivity() {

    val availableDays = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_post_service_request)
        setUpPostServiceRequestActivity()
    }

    private fun setUpPostServiceRequestActivity() {
        setUpToolBar()
        setUpFormSpinners()
        setUpAvailabilityDialog()
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

    private fun setUpAvailabilityDialog() {
        id_calendar_icon.setOnClickListener {
            displayAvailabilityDialog()
        }

        id_availability.setOnClickListener {
            displayAvailabilityDialog()
        }
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
                    com.example.mobilemechanic.R.layout.support_simple_spinner_dropdown_item,
                    vehicles
                )
        }
    }

    private fun displayAvailabilityDialog() {
        val dialogContainer = layoutInflater.inflate(R.layout.basic_dialog, null)
        val availabilityBody = layoutInflater.inflate(R.layout.dialog_body_availability, null)
        dialogContainer.apply {
            id_title.text = "Availability"
            id_negative.text = "Cancel"
            id_positive.text = "Save"
        }

        val availabilityDialog = BasicDialog(this, dialogContainer, availabilityBody)
        availabilityDialog.show()

        dialogContainer.id_positive.setOnClickListener {
            availableDays.clear()
            if (availabilityBody.id_mon_checkbox.isChecked) {
                availableDays.add("mon")
            }

            if (availabilityBody.id_tues_checkbox.isChecked) {
                availableDays.add("tues")
            }

            Log.d("TEST", availableDays.toString())

            availabilityDialog.dismiss()
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
        id_warning_message.visibility = View.GONE
        id_warning_message_add.visibility = View.GONE
    }

    private fun showWarningIconAndMessage() {
        id_warning_icon.visibility = View.VISIBLE
        id_warning_message.visibility = View.VISIBLE
        id_warning_message_add.visibility = View.VISIBLE
    }

    private fun isGarageEmpty(vehicles: List<String>): Boolean {
        if (vehicles.size <= 1) {
            return true
        }
        return false
    }
}
