package com.example.mobilemechanic.client.postservicerequest

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.client.findservice.EXTRA_SERVICE
import com.example.mobilemechanic.client.garage.GarageActivity
import com.example.mobilemechanic.model.ServiceModel
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.activity_post_service_request.*
import kotlinx.android.synthetic.main.basic_dialog.view.*
import kotlinx.android.synthetic.main.dialog_body_availability.view.*
import java.util.*
import kotlin.collections.ArrayList

const val POST_SERVICE_TAG = "postservice"
class PostServiceRequestActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener  {

    private val availableDays = ArrayList<String>()
    private lateinit var dialogContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_post_service_request)
        setUpPostServiceRequestActivity()
    }

    private fun setUpPostServiceRequestActivity() {
        setUpActionBar()
        setUpVehicleSpinner()
        setUpAvailabilityDialog()
        setUpServiceParcel()
        setUpOnSubmit()
        setUpOnAddVehicle()
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_service_form_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpAvailabilityDialog() {
//        val dialogContainer = layoutInflater.inflate(R.layout.basic_dialog, null)
        dialogContainer = layoutInflater.inflate(R.layout.basic_dialog, null)
        val dialogBody = layoutInflater.inflate(R.layout.dialog_body_availability, null)
        val basicDialog = BasicDialog.Builder.apply {
            title = "Availability"
            positive = "Confirm"
            negative = "Cancel"
        }.build(this, dialogContainer, dialogBody)

        handleDialogPopup(basicDialog)
        handleDialogClicked(basicDialog, dialogContainer, dialogBody)
    }

    private fun setUpServiceParcel() {
        val service = intent.getParcelableExtra<ServiceModel>(EXTRA_SERVICE)
        id_mechanic_name.text = service.mechanicName
        id_service_type.text = service.serviceType
        id_service_description.text = service.description
        id_service_price.text = "$${service.price.toInt()}"
        id_mechanic_rating.text = service.rating.toString()
    }

    private fun setUpOnSubmit() {
        id_submit.setOnClickListener {
            validateForm()
            val service = intent.getParcelableExtra<ServiceModel>(EXTRA_SERVICE)
            val vehicle = id_vehicle_spinner.selectedItem.toString()
            val comment = id_comment.text
            Log.d(POST_SERVICE_TAG, "service: $service\nvehicle: $vehicle\ncomment: $comment")

            // Create request and submit to database.

            startActivity(Intent(this, ClientWelcomeActivity::class.java))
        }
    }

    private fun setUpOnAddVehicle() {
        id_warning_message_add.setOnClickListener {
            startActivity(Intent(this, GarageActivity::class.java))
        }
    }

    private fun validateForm() {
        if ((id_vehicle_spinner.selectedItemPosition == 0)) {
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
            var checkBoxArray = listOf(
                dialogBody.id_mon_checkbox,
                dialogBody.id_tues_checkbox,
                dialogBody.id_wed_checkbox,
                dialogBody.id_thur_checkbox,
                dialogBody.id_fri_checkbox,
                dialogBody.id_sat_checkbox,
                dialogBody.id_sun_checkbox
            )

            var daysOfWeek = listOf("mon","tues","wed","thur","fri","sat","sun")

            // add checked days of week to an array
            for ((index, day) in checkBoxArray.withIndex()) {
                if (day.isChecked) {
                    availableDays.add(daysOfWeek[index])
                }
            }
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

    fun clickTimePicker(view: View) {
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener { v, h, m ->
            val time = (if (h > 12) "${h % 12}:" else "${h}:").toString() +
                    (if (m < 10) "0${m}" else "${m}").toString() +
                    (if (h >= 12) " PM" else " AM").toString()

            when (view.id) {
                dialogContainer.id_btnFromTime.id -> {
                    dialogContainer.id_btnFromTime.text = time.toString()
                }
                dialogContainer.id_btnToTime.id -> {
                    dialogContainer.id_btnToTime.text = time.toString()
                }
            }

        },hour,minute,false)

        tpd.show()
    }

}
