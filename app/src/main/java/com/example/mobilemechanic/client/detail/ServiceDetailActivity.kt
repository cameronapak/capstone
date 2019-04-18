package com.example.mobilemechanic.client.detail

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.mechanic.map.MY_PERMISSION_REQ_GPS
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.messaging.ChatRoomsActivity
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_service_detail.*
import kotlinx.android.synthetic.main.card_vehicle_details_container.*
import kotlinx.android.synthetic.main.card_vehicle_details_container.view.*
import kotlinx.android.synthetic.main.dialog_body_contact.*
import kotlinx.android.synthetic.main.dialog_container_basic_single.*

class ServiceDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var request: Request
    private lateinit var mMap: GoogleMap
    private lateinit var basicDialog: Dialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference
    val REQUEST_PHONE_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)

        setUpServiceDetailActivity()
    }

    private fun setUpServiceDetailActivity() {
        initFireStore()
        getRequestParcel()
        getRequest()
        setUpMap()
    }

    private fun getRequestParcel() {
        request = intent.getParcelableExtra(EXTRA_REQUEST)
    }

    private fun initFireStore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection("Requests")
    }

    private fun getRequest() {
        requestRef.document(request.objectID).get()
            .addOnSuccessListener {
                val request = it.toObject(Request::class.java)!!

                request.objectID = it.id
                Log.d(CLIENT_TAG, "[ServiceDetailActivity] Request ID: ${request.objectID}")

                setUpVehicleContainer()
                setUpToolBar(request)
            }
            .addOnFailureListener {
                finish()
            }
    }

    private fun setUpVehicleContainer()
    {
        val holder = id_client_more_info_card
        Picasso.get().load(request?.vehicle?.photoUrl).into(holder.id_client_car_image)
        holder.id_client_car_title.text = "${request.vehicle?.year} ${request.vehicle?.make} ${request.vehicle?.model}"
        holder.id_service_completed.text = request.service?.serviceType
        val address = request.clientInfo!!.address
        holder.id_client_address.text = "${address.street}\n${address.city}, ${address.state} ${address.zipCode}"
        holder.id_mechanic.text = "${request.mechanicInfo!!.basicInfo.firstName} ${request.mechanicInfo!!.basicInfo.lastName}"

        holder.id_contact.setOnClickListener {
            setUpContactServiceDialog(request)
        }
        getReceiptSummary()
    }

    private fun getReceiptSummary() {
        var sub = 0.00F
        var tax = 0.00F
        var total = 0.00F
        id_summary_subtotal_price.text = String.format("%s %.2f", "$", sub)
        id_summary_estimated_tax_price.text = String.format("%s %.2f", "$", tax)
        id_grand_total_price.text = String.format("%s %.2f", "$", total)
    }

    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        val clientAddress = request.clientInfo!!.address
        val clientLatLng = LatLng(clientAddress._geoloc?.lat, clientAddress._geoloc?.lng)

        val cameraPosition = CameraPosition.Builder()
            .target(clientLatLng).zoom(16f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(clientLatLng))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        when(requestCode)
        {
            MY_PERMISSION_REQ_GPS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        mMap.isMyLocationEnabled = true
                    }
                    catch(ex: SecurityException)
                    {
                        Log.d(com.example.mobilemechanic.mechanic.map.TAG, ex.toString())
                    }
                }
            }

            REQUEST_PHONE_CALL->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startCall(request)
                else//request permission
                    //context, constant for access call, permission request code
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                        REQUEST_PHONE_CALL)
            }
        }
    }

    private fun setUpContactServiceDialog(request: Request) {

        val dialogContainer =
            layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic_single, null)
        val dialogBody = layoutInflater.
            inflate(com.example.mobilemechanic.R.layout.dialog_body_contact, null)

        basicDialog = BasicDialog.Builder.apply {
            title = "Contact"
            positive = "Call"
            negative = "Message"
        }.buildSingle(this, dialogContainer, dialogBody)

        val basicInfo = request.mechanicInfo!!.basicInfo
        basicDialog.id_phone_number.text = basicInfo.phoneNumber
        basicDialog.id_show_client_name.text = "${basicInfo.firstName}  ${basicInfo.lastName}"

        if (basicInfo?.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.ic_circle_profile).into(basicDialog.id_contact_user_profile_image)
        } else {
            Picasso.get().load(Uri.parse(basicInfo?.photoUrl)).into(basicDialog.id_contact_user_profile_image)
        }

        basicDialog.show()
        handleDialogOnClick(basicDialog)
    }

    private fun handleDialogOnClick(basicDialog: Dialog) {
        basicDialog.id_cancel.setOnClickListener {
            basicDialog.dismiss()
        }

        basicDialog.id_message.setOnClickListener {
            val intent = Intent(this, ChatRoomsActivity::class.java)
            intent.putExtra(EXTRA_USER_TYPE, UserType.CLIENT.name)
            startActivity(intent)
        }

        basicDialog.id_call.setOnClickListener {
            startCall(request)
        }
    }

    private fun startCall(request: Request){
        val phoneNum = request.mechanicInfo?.basicInfo?.phoneNumber
            ?.replace("[^0-9\\+]".toRegex(), "")
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNum"))

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
        {
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_PHONE_CALL)
        }
    }

    private fun setUpToolBar(request: Request) {
        setSupportActionBar(id_service_detail_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Service Details"
            subtitle = "Serviced on ${DateTimeManager.millisToDate(request.completedOn, "MMM d, y")}"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
