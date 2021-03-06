package com.example.mobilemechanic.mechanic.map

import android.Manifest
import android.annotation.SuppressLint
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
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.mechanic.MECHANIC_TAG
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.Toasty
import com.example.mobilemechanic.shared.ToastyType
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.AuthenticationManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_mechanic_manage_job.*
import kotlinx.android.synthetic.main.card_vehicle_container.view.*
import kotlinx.android.synthetic.main.dialog_body_contact.*
import kotlinx.android.synthetic.main.dialog_container_basic.*


class MechanicManageJobActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference
    private lateinit var basicDialog: Dialog

    private lateinit var request: Request

    val REQUEST_PHONE_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_manage_job)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection(getString(R.string.ref_requests))
        //get request from MechanicWelcomeActivity
        request = intent.getParcelableExtra(EXTRA_REQUEST)

        setUpMechanicManageJobActivity()
    }

    private fun setUpMechanicManageJobActivity() {
        setUpToolBar()
        setUpVehicleContainer()
    }


    private fun setUpToolBar() {
        setSupportActionBar(id_manage_job_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Manage Job"
            subtitle = "Cancel the job or contact the client"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpVehicleContainer() {
        val holder = id_mechanic_manage_job_card
        holder.id_car_title.text = "${request.vehicle?.year} ${request.vehicle?.make} ${request.vehicle?.model}"
        holder.id_service_needed.text = request.service?.serviceType
        val address = request.clientInfo!!.address
        holder.id_address.text = "${address.street}\n${address.city}, ${address.state} ${address.zipCode}"

        var availability = request.clientInfo!!.availability
        var availabilityText = ""
        availability.days.forEach { day -> availabilityText += "$day " }
        availabilityText += "\n${availability.fromTime} to ${availability.toTime}"
        holder.id_availability.text = availabilityText

        holder.id_positive.text = getString(R.string.text_contact)
        holder.id_positive.setOnClickListener {
            setUpContactServiceDialog()
        }

        holder.id_negative.text = getString(R.string.text_cancel_job)
        holder.id_negative.setOnClickListener {
            createCancelJobDialog()
        }
    }


    @SuppressLint("MissingPermission")
    private fun setUpContactServiceDialog() {

        val dialogContainer =
            layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)
        val dialogBody = layoutInflater.
            inflate(com.example.mobilemechanic.R.layout.dialog_body_contact, null)

        basicDialog = BasicDialog.Builder.apply {
            title = "Contact"
            positive = "Call"
            negative = "Cancel"
        }.build(this, dialogContainer, dialogBody)

        val basicInfo = request.clientInfo!!.basicInfo
        basicDialog.id_phone_number.text = basicInfo.phoneNumber
        basicDialog.id_show_client_name.text = "${basicInfo.firstName}  ${basicInfo.lastName}"

        if (basicInfo?.photoUrl.isNullOrEmpty()) {
            Log.d(MECHANIC_TAG, "[MechanicManagerJobActivity] client photo ${basicInfo.photoUrl}")
            Picasso.get().load(R.drawable.ic_circle_profile).into(basicDialog.id_contact_user_profile_image)
        } else {
            Picasso.get().load(Uri.parse(basicInfo?.photoUrl)).into(basicDialog.id_contact_user_profile_image)
        }

        basicDialog.show()

        basicDialog.id_positive.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startCall()
            }
            else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_PHONE_CALL)
            }
        }
        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }
    }

    @SuppressLint("MissingPermission")
    fun startCall(){
        val phoneNum = request.clientInfo?.basicInfo?.phoneNumber
            ?.replace("[^0-9\\+]".toRegex(), "")

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum))
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_REQ_GPS)
        }

        val clientAddress = request.clientInfo?.address
        val clientLatLng = AddressManager.convertAddressToLatLng(this, clientAddress)

        val cameraPosition = CameraPosition.Builder()
            .target(clientLatLng).zoom(16f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(clientLatLng))
        ScreenManager.toggleVisibility(id_progress_bar)
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
                        Log.d(TAG, ex.toString())
                    }
                }
            }

            REQUEST_PHONE_CALL->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(MECHANIC_TAG, grantResults.toString())
                    startCall()
                }

            }
        }
    }

    private fun createCancelJobDialog()
    {
        val container = layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val dialogBody = layoutInflater.inflate(R.layout.dialog_body_choice, null)
        val cancelJobDialog = BasicDialog.Builder.apply{
            title = getString(R.string.text_cancel_job)
            positive = getString(R.string.yes)
            negative = getString(R.string.text_cancel)
        }.build(this, container, dialogBody)
        cancelJobDialog.show()

        cancelJobDialog.id_positive.setOnClickListener {
            cancelJob()
            cancelJobDialog.dismiss()
        }

        cancelJobDialog.id_negative.setOnClickListener {
            cancelJobDialog.dismiss()
        }
    }

    private fun cancelJob()
    {
        requestRef.document(request.objectID)
            .update("status", Status.Cancelled)
            ?.addOnSuccessListener {
                Toasty.makeText(this, "Job was canceled", ToastyType.SUCCESS)
                finish()
            }
            .addOnFailureListener {
                Toasty.makeText(this, "Unable to cancel", ToastyType.FAIL)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        AuthenticationManager.signInGuard(this)
        super.onResume()
    }
}
