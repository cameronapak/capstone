package com.example.mobilemechanic.mechanic.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mechanic_manage_job.*
import kotlinx.android.synthetic.main.card_vehicle_container.view.*
import kotlinx.android.synthetic.main.dialog_body_contact.view.*
import kotlinx.android.synthetic.main.dialog_container_basic.*
import android.content.Intent
import android.net.Uri
import kotlinx.android.synthetic.main.dialog_body_contact.*


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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection(getString(R.string.ref_requests))
        //get request from MechanicWelcomeActivity
        request = intent.getParcelableExtra(EXTRA_REQUEST)

        setUpMechanicManageJobActivity()
    }

    private fun setUpMechanicManageJobActivity()
    {
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

    private fun setUpVehicleContainer()
    {
        val holder = id_mechanic_manage_job_card
        holder.id_car_title.text = "${request.vehicle?.year} ${request.vehicle?.make} ${request.vehicle?.model}"
        //TODO: holder.id_car_image.setImageURI()
        holder.id_service_needed.text = request.service?.serviceType
        val address = request.clientInfo!!.address
        holder.id_address.text = "${address.street}\n${address.city}, ${address.state} ${address.zipCode}"

        var availability = request.clientInfo!!.availability
        var availabilityText = ""
        availability.days.forEach { day -> availabilityText += "$day " }
        availabilityText += "\n${availability.fromTime} to ${availability.toTime}"
        holder.id_availability.text = availabilityText
        //Log.d(MECHANIC_TAG, "${availability.days}")

        holder.id_positive.text = getString(R.string.text_contact)
        holder.id_positive.setOnClickListener {
            //TODO: Contact Customer Dialog
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

        val showPhone= request.clientInfo!!.basicInfo!!.phoneNumber
        basicDialog.id_phone_number.text = showPhone

        basicDialog.show()

        basicDialog.id_positive.setOnClickListener {
                startCall()
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


        //add zoom button
        mMap.uiSettings.isZoomControlsEnabled = true

        //requires permission to use GPS
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.isMyLocationEnabled = true
        }
        else//request permission
        {
            //context, constant for access fine location value, permission request code
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_REQ_GPS)
        }

        //convert the address string into latitude, longitude pair for google maps
        val address = request.clientInfo!!.address
        val clientAddress = "${address.street} ${address.city}, ${address.state} ${address.zipCode}"
        val clientLatLng = AddressManager.convertAddress(this, clientAddress)

        //zoom into client's location on the map
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
                        Log.d(TAG, ex.toString())
                    }
                }
            }

            REQUEST_PHONE_CALL->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startCall()
                }

            }
        }
    }

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
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
                Toast.makeText(this, "Canceled job successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.err_cancel_job_fail), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
