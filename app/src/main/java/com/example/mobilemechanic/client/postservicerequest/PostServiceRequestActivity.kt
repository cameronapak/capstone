package com.example.mobilemechanic.client.postservicerequest

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.findservice.EXTRA_MECHANIC
import com.example.mobilemechanic.client.findservice.EXTRA_SERVICE
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.activity_post_service_request.*
import kotlinx.android.synthetic.main.basic_dialog.view.*
import kotlinx.android.synthetic.main.dialog_body_availability.view.*


class PostServiceRequestActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val availableDays = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_post_service_request)
        setUpPostServiceRequestActivity()
    }

    private fun setUpPostServiceRequestActivity() {
        setUpActionBar()
        setUpVehicleSpinner()
        setUpAvailabilityDialog()
        setUpServiceSelected()
        setUpOnSubmit()
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_service_form_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpAvailabilityDialog() {
        val dialogContainer = layoutInflater.inflate(R.layout.basic_dialog, null)
        val dialogBody = layoutInflater.inflate(R.layout.dialog_body_availability, null)
        val basicDialog = BasicDialog.Builder.apply {
            title = "My Title"
            positive = "Confirm"
            negative = "Cancel"
        }.build(this, dialogContainer, dialogBody)

        handleDialogPopup(basicDialog)
        handleDialogClicked(basicDialog, dialogContainer, dialogBody)
    }

    private fun setUpServiceSelected() {

        val mechanicSelected = intent.getParcelableExtra<User>(EXTRA_MECHANIC)
        val service = intent.getParcelableExtra<Service>(EXTRA_SERVICE)

    }

    private fun setUpOnSubmit() {
        id_submit.setOnClickListener {
            val description = id_description.text.toString()
            val vehicle = id_vehicle_spinner.selectedItem.toString()
            val mechanic = intent.getParcelableExtra<User>(EXTRA_MECHANIC)
            val service = intent.getParcelableExtra<Service>(EXTRA_SERVICE)

            Log.d("TEST", "$description \n $vehicle \n $mechanic \n $service")




        }
    }

    private fun validateForm() {
        if ((id_vehicle_spinner.selectedItemPosition == 0) or (availableDays.isEmpty())) {
                disableSubmitButton()
        } else {
                enableSubmitButton()
        }
    }


    private fun setUpVehicleSpinner() {
        id_vehicle_spinner.onItemSelectedListener = this
        val vehicles = arrayOf("Vehicle","2011 Toyota Venza", "2013 Toyota Camry")
              .asList()

//        val vehicles = arrayOf("Vehicle").asList()

        id_vehicle_spinner.adapter =
            HintSpinnerAdapter(this, R.layout.support_simple_spinner_dropdown_item, vehicles)
        if (isGarageEmpty(vehicles)) {
            showWarningIconAndMessage()
        } else {
            hideWarningIconAndMessage()
        }
    }

    private fun handleDialogPopup(basicDialog: Dialog) {
        id_calendar_icon.setOnClickListener {
            basicDialog.show()
        }

        id_availability.setOnClickListener {
            basicDialog.show()
        }
    }

    private fun handleDialogClicked(basicDialog: Dialog, dialogContainer: View, dialogBody: View) {
        dialogContainer.id_positive.setOnClickListener {
            validateForm()
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

    private fun disableSubmitButton() {
        id_submit.isEnabled = false
        id_submit.setBackgroundResource(R.drawable.button_round_corner_disabled)
    }

    private fun enableSubmitButton() {
        id_submit.isEnabled = true
        id_submit.setBackgroundResource(R.drawable.button_round_corner)
    }

    private fun isGarageEmpty(vehicles: List<String>): Boolean {
        if (vehicles.size <= 1) {
            return true
        }
        return false
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        validateForm()
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
