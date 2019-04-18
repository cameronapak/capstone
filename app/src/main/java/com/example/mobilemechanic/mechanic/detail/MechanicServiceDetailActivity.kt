package com.example.mobilemechanic.mechanic.detail

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
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
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

class MechanicServiceDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var request: Request
    private lateinit var mMap: GoogleMap
    private lateinit var basicDialog: Dialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference
    val REQUEST_PHONE_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_service_detail)

        setUpMechanicServiceDetailActivity()
    }

    private fun setUpMechanicServiceDetailActivity() {
        getRequestParcel()
        setUpToolBar()
        setUpMap()
        setUpDetailsContainer()
    }

    private fun getRequestParcel() {
        request = intent.getParcelableExtra(EXTRA_REQUEST)
    }


    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setUpDetailsContainer() {
        val holder = id_client_more_info_card
        Picasso.get().load(request?.vehicle?.photoUrl).into(holder.id_client_car_image)
        holder.id_client_car_title.text = "${request.vehicle?.year} ${request.vehicle?.make} ${request.vehicle?.model}"
        holder.id_service_completed.text = request.service?.serviceType
        val address = request.clientInfo!!.address
        holder.id_client_address.text = "${address.street}\n${address.city}, ${address.state} ${address.zipCode}"
        holder.id_name.text = "Client"
        holder.id_mechanic.text = "${request.clientInfo!!.basicInfo.firstName} ${request.clientInfo!!.basicInfo.lastName}"

        holder.id_contact.setOnClickListener {
            openContactDialog()
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        val clientAddress = request.clientInfo!!.address
        val clientLatLng = LatLng(clientAddress._geoloc.lat, clientAddress._geoloc.lng)
        val cameraPosition = CameraPosition.Builder()
            .target(clientLatLng).zoom(16f).build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(clientLatLng))
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_service_detail_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Service Details"
            subtitle = "Service on ${DateTimeManager.millisToDate(request.completedOn, "MMM d, y")}"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun openContactDialog() {
        val dialogContainer =
            layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic_single, null)
        val dialogBody = layoutInflater.
            inflate(com.example.mobilemechanic.R.layout.dialog_body_contact, null)

        basicDialog = BasicDialog.Builder.apply {
            title = "Contact"
        }.buildSingle(this, dialogContainer, dialogBody)

        val basicInfo = request.clientInfo!!.basicInfo
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
            intent.putExtra(EXTRA_USER_TYPE, UserType.MECHANIC.name)
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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
            PackageManager.PERMISSION_GRANTED) {
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_PHONE_CALL)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
