package com.example.mobilemechanic.client.postservicerequest

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.findservice.EXTRA_MECHANIC
import com.example.mobilemechanic.shared.BasicDialog
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
        val mechanicSelected = intent.getSerializableExtra(EXTRA_MECHANIC)
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
        val dialogContainer = layoutInflater.inflate(R.layout.basic_dialog, null)
        val dialogBody = layoutInflater.inflate(R.layout.dialog_body_availability, null)
        val basicDialog = BasicDialog.Builder.apply {
            title = "My Title"
            positive = "Confirm"
            negative = "Cancel"
        }.build(this, dialogContainer, dialogBody)

        // Show the dialog
        id_calendar_icon.setOnClickListener {
            basicDialog.show()
        }

        id_availability.setOnClickListener {
            basicDialog.show()
        }


        // Handle/Retrieve data from the dialog body
        dialogContainer.id_positive.setOnClickListener {

            availableDays.clear()
            if (dialogBody.id_mon_checkbox.isChecked) {
                availableDays.add("mon")
            }

            Log.d("TEST", availableDays.toString())
            basicDialog.dismiss()
        }

        dialogContainer.id_negative.setOnClickListener {
            basicDialog.dismiss()
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
