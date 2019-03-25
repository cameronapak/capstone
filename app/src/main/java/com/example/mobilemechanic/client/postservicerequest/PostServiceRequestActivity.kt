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
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.Vehicle
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.example.mobilemechanic.model.dto.Availability
import com.example.mobilemechanic.model.dto.ClientInfo
import com.example.mobilemechanic.model.messaging.ChatRoom
import com.example.mobilemechanic.model.messaging.Member
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintVehicleSpinnerAdapter
import com.example.mobilemechanic.shared.utility.ObjectConverter
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_post_service_request.*
import kotlinx.android.synthetic.main.dialog_body_availability.view.*
import kotlinx.android.synthetic.main.dialog_container_basic.view.*
import java.util.*
import kotlin.collections.ArrayList

class PostServiceRequestActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var accountRef: CollectionReference
    private lateinit var requestsRef: CollectionReference
    private lateinit var vehiclesRef: CollectionReference
    private lateinit var chatRoomsRef: CollectionReference
    private lateinit var spinnerAdapter: HintVehicleSpinnerAdapter
    private val availableDays = ArrayList<String>()
    private lateinit var dialogContainer: View
    private lateinit var daysOfWeekString: String
    private var fromTime: String = ""
    private var toTime: String = ""
    private lateinit var serviceModel: ServiceModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_post_service_request)
        mFirestore = FirebaseFirestore.getInstance()
        requestsRef = mFirestore.collection("Requests")
        mAuth = FirebaseAuth.getInstance()
        vehiclesRef = mFirestore.collection("Accounts/${mAuth.currentUser?.uid}/Vehicles")
        accountRef = mFirestore.collection("Accounts")
        chatRoomsRef = mFirestore.collection("ChatRooms")
        serviceModel = intent.getParcelableExtra(EXTRA_SERVICE)

        Log.d(CLIENT_TAG, "[PostServiceRequestActivity] User uid: ${mAuth.currentUser?.uid}")
        Log.d(CLIENT_TAG, "[PostServiceRequestActivity] User email: ${mAuth.currentUser?.email}")
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
            id_mechanic_name.text =
                "${serviceModel.mechanicInfo.basicInfo.firstName} ${serviceModel.mechanicInfo.basicInfo.lastName}"
            id_service_type.text = serviceModel.service.serviceType
            id_service_description.text = serviceModel.service.description
            id_price.text = "$${serviceModel.service.price.toInt()}"
            id_mechanic_rating.rating = serviceModel.mechanicInfo.rating
        }
    }

    private fun setUpOnSubmit() {
        id_submit.setOnClickListener {
            validateForm()
            val mechanicInfo = serviceModel.mechanicInfo
            val vehicle = id_vehicle_spinner.selectedItem as Vehicle
            val comment = id_comment.text.toString()
            val currentTime = System.currentTimeMillis()

            accountRef.document(mAuth.currentUser?.uid.toString())
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }

                    val clientInfo = extractUserInfo(snapshot)
                    if (clientInfo != null) {
                        val request = Request.Builder()
                            .clientInfo(clientInfo!!)
                            .mechanicInfo(serviceModel.mechanicInfo)
                            .service(serviceModel.service)
                            .vehicle(vehicle)
                            .comment(comment)
                            .status(Status.Request)
                            .postedOn(currentTime)
                            .build()

                        Log.d(CLIENT_TAG, "$request)")
                        requestsRef.document().set(request).addOnSuccessListener {
                            Toast.makeText(this, "Request sent successfully", Toast.LENGTH_LONG).show()


                            val clientMember = ObjectConverter.convertToMember(clientInfo)
                            val mechanicMember = ObjectConverter.convertToMember(mechanicInfo)

                            setUpChatRoom(clientMember, mechanicMember)

                        }.addOnFailureListener {
                            Toast.makeText(this, "Request failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    private fun extractUserInfo(snapshot: DocumentSnapshot?): ClientInfo? {
        var availability = Availability(fromTime, toTime, availableDays)
        if (snapshot != null && snapshot.exists()) {
            val client = snapshot.toObject(User::class.java)
            if (client != null) {
                return ClientInfo(
                    client.uid,
                    client.basicInfo,
                    availability,
                    client.address
                )
            }
        }
        return null
    }

    private fun setUpChatRoom(client: Member, mechanic: Member)
    {
        checkIfChatRoomExist(client, mechanic)
    }

    private fun checkIfChatRoomExist(client: Member, mechanic: Member) {
        chatRoomsRef.whereEqualTo("clientMember.uid", client.uid)
        chatRoomsRef.whereEqualTo("mechanicMember.uid", mechanic.uid)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    Log.d(CLIENT_TAG, "[PostServiceRequestActivity] no chat room exist yet")
                    createChatRoom(client, mechanic)
                } else {
                    startActivity(Intent(this, ClientWelcomeActivity::class.java))
                    finish()
                    Log.d(CLIENT_TAG, "[PostServiceRequestActivity] chat room already exist")
                }
            }
    }

    private fun createChatRoom(client: Member, mechanic: Member)
    {
        val docID = chatRoomsRef.document().id
        val chatRoom = ChatRoom(docID, client, mechanic)
        chatRoomsRef.document(docID).set(chatRoom)
            .addOnSuccessListener {
                Log.d(CLIENT_TAG, "[PostServiceRequestActivity] chat room created")
                startActivity(Intent(this, ClientWelcomeActivity::class.java))
                finish()
            }.addOnFailureListener {
                Log.d(CLIENT_TAG, "[PostServiceRequestActivity] fail to create a chat room")
            }

    }

    private fun setUpOnAddVehicle() {
        id_warning_message_add.setOnClickListener {
            startActivity(Intent(this, GarageActivity::class.java))
        }
    }

    private fun validateForm() {
        id_submit.isEnabled = (id_vehicle_spinner.selectedItemPosition != 0)
    }

    private fun setUpVehicleSpinner() {
        val vehicles = ArrayList<Vehicle>()
        id_vehicle_spinner.onItemSelectedListener = this
        spinnerAdapter = HintVehicleSpinnerAdapter(this, R.layout.support_simple_spinner_dropdown_item, vehicles)
        vehiclesRef.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }
            vehicles.clear()
            vehicles.add(Vehicle("", "", "", "", "")) //Dummy hint

            for (doc in querySnapshot!!) {
                val vehicle = doc.toObject(Vehicle::class.java)
                vehicle.objectID = doc.id
                vehicles.add(vehicle)
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
            if (availableDays.isEmpty()) {
                Toast.makeText(this, "Select available days", Toast.LENGTH_SHORT).show()
            } else if (fromTime.isNullOrBlank() || toTime.isNullOrBlank()) {
                Toast.makeText(this, "Select from and to times", Toast.LENGTH_SHORT).show()
            } else {
                id_availability_result.text = "$daysOfWeekString $fromTime to $toTime"
                basicDialog.dismiss()
            }
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

    private fun checkIfGarageIsEmpty(vehicles: ArrayList<Vehicle>) {
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
                    (if (m < 10) "0$m" else "$m").toString() +
                    (if (h >= 12) " PM" else " AM").toString()

            when (view.id) {
                dialogContainer.id_btnFromTime.id -> {
                    dialogContainer.id_btnFromTime.text = time
                    fromTime = time
                }
                dialogContainer.id_btnToTime.id -> {
                    dialogContainer.id_btnToTime.text = time
                    toTime = time
                }
            }

        }, hour, minute, false)

        tpd.show()
    }

    private fun enableHideKeyboard() {
        id_post_service_frame_layout.setOnClickListener {
            ScreenManager.hideKeyBoard(this)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        validateForm()
    }

    override fun onResume() {
        super.onResume()
        hideWarningIconAndMessage()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        enableHideKeyboard()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
