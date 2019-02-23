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
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.client.findservice.EXTRA_SERVICE
import com.example.mobilemechanic.client.garage.GarageActivity
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.model.Vehicle
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_post_service_request.*
import kotlinx.android.synthetic.main.dialog_body_availability.view.*
import kotlinx.android.synthetic.main.dialog_container_basic.view.*
import java.util.*
import kotlin.collections.ArrayList

const val POST_SERVICE_TAG = "postservice"
const val HINT_VEHICLE = "Vehicle"
class PostServiceRequestActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestsRef: CollectionReference
    private lateinit var vehiclesRef: CollectionReference
    private lateinit var spinnerAdapter: HintSpinnerAdapter
    private val availableDays = ArrayList<String>()
    private lateinit var dialogContainer: View
    private lateinit var daysOfWeekString: String
    private lateinit var fromTime: String
    private lateinit var toTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_post_service_request)
        mFirestore = FirebaseFirestore.getInstance()
        requestsRef = mFirestore.collection("Requests")
        mAuth = FirebaseAuth.getInstance()
        vehiclesRef = mFirestore.collection("Accounts/${mAuth.currentUser?.uid}/Vehicles")

        Log.d(CLIENT_TAG, "[PostServiceRequestActivity] User uid: ${mAuth?.currentUser?.uid}")
        Log.d(CLIENT_TAG, "[PostServiceRequestActivity] User email: ${mAuth?.currentUser?.email}")
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
        setSupportActionBar(id_find_service_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpAvailabilityDialog() {
        dialogContainer = layoutInflater.inflate(R.layout.dialog_container_basic, null)
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
        if (intent.hasExtra(EXTRA_SERVICE)) {
            val service = intent.getParcelableExtra<ServiceModel>(EXTRA_SERVICE)
            id_mechanic_name.text = service.mechanicFirstName
            id_service_type.text = service.serviceType
            id_service_description.text = service.description
            id_price.text = "$${service.price.toInt()}"
            id_mechanic_rating.text = service.rating.toString()
        }
    }

    private fun setUpOnSubmit() {
        id_submit.setOnClickListener {
            validateForm()
            val service = intent.getParcelableExtra<ServiceModel>(EXTRA_SERVICE)
            val vehicle = id_vehicle_spinner.selectedItem.toString()
            val comment = id_comment.text
            Log.d(POST_SERVICE_TAG, "service: $service\nvehicle: $vehicle\ndescription: $comment")
            val clientId = mAuth?.currentUser?.uid
            val currentTime = System.currentTimeMillis()/1000
            var clientPhotoUrl = mAuth?.currentUser?.photoUrl.toString()
            if (clientId != null) {
                val request = Request(
                    "requestID",
                    clientId,
                    clientPhotoUrl,
                    service.uid,
                    service.description,
                    vehicle,
                    service.serviceType,
                    Status.Request,
                    currentTime,
                    -1,
                    "$fromTime to $toTime",
                    "$daysOfWeekString"
                )
                Log.d(CLIENT_TAG, "[PostServiceRequestActivity] request $request")

                requestsRef.document().set(request).addOnSuccessListener {
                    Toast.makeText(this, "Request sent successfully", Toast.LENGTH_LONG)
                    startActivity(Intent(this, ClientWelcomeActivity::class.java))
                    finish()
                }?.addOnFailureListener {
                    Log.d(CLIENT_TAG, it.message)
                    Toast.makeText(this, "Request failed", Toast.LENGTH_LONG)
                }
            }
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
        val vehicles = ArrayList<String>()
        id_vehicle_spinner.onItemSelectedListener = this
        spinnerAdapter = HintSpinnerAdapter(this, R.layout.support_simple_spinner_dropdown_item, vehicles)
        vehiclesRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }
            vehicles.clear()
            vehicles.add(HINT_VEHICLE)
            for (doc in querySnapshot!!) {
                val vehicle = doc.toObject(Vehicle::class.java)
                vehicle.objectID = doc.id
                vehicles.add(vehicle.toString())
                Log.d(CLIENT_TAG, "[PostServiceRequestActivity] snapshotListener vehicle objectID: ${vehicle.objectID}")
            }
            spinnerAdapter.notifyDataSetChanged()
            checkIfGarageIsEmpty(vehicles)
        }

        id_vehicle_spinner.adapter = spinnerAdapter
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

            var daysOfWeek = listOf("mon", "tues", "wed", "thur", "fri", "sat", "sun")
            for ((index, day) in checkBoxArray.withIndex()) {
                if (day.isChecked) {
                    availableDays.add(daysOfWeek[index])
                }
            }

            daysOfWeekString = availableDays.joinToString(separator = ", ")
            fromTime = dialogContainer.id_btnFromTime.text.toString()
            toTime = dialogContainer.id_btnToTime.text.toString()
            id_availability_result.text = "$daysOfWeekString $fromTime to $toTime"
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

    private fun checkIfGarageIsEmpty(vehicles: ArrayList<String>) {
        if (vehicles.size <= 1) {
            showWarningIconAndMessage()
        } else {
            hideWarningIconAndMessage()
        }
    }

    fun clickTimePicker(view: View) {
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { v, h, m ->
            val time = (if (h > 12) "${h % 12}:" else "${if (h == 0) "12" else h}:").toString() +
                    (if (m < 10) "0${m}" else "${m}").toString() +
                    (if (h >= 12) " PM" else " AM").toString()

            // TODO: compare times from and to, and do not allow continue unless from is before to

            when (view.id) {
                dialogContainer.id_btnFromTime.id -> {
                    dialogContainer.id_btnFromTime.text = time
                }
                dialogContainer.id_btnToTime.id -> {
                    dialogContainer.id_btnToTime.text = time
                }
            }

        }, hour, minute, false)

        tpd.show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        validateForm()
    }

    override fun onResume() {
        super.onResume()
        hideWarningIconAndMessage()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
