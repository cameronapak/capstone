package com.example.mobilemechanic.mechanic.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
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
import kotlinx.android.synthetic.main.activity_mechanic_more_information.*
import kotlinx.android.synthetic.main.card_vehicle_container.view.*
import kotlinx.android.synthetic.main.dialog_container_basic.*

const val MY_PERMISSION_REQ_GPS = 1
const val TAG = "moreInfo"

class MechanicMoreInformationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference

    private lateinit var request: Request

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_more_information)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection(getString(R.string.ref_requests))
        request = intent.getParcelableExtra(EXTRA_REQUEST)

        setUpMechanicMoreInformationActivity()
    }

    private fun setUpMechanicMoreInformationActivity() {
        setUpToolBar()
        setUpVehicleContainer()
    }


    private fun setUpToolBar() {
        setSupportActionBar(id_more_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "More Information"
            subtitle = "Request details"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpVehicleContainer() {
        val holder = id_mechanic_more_info_card
        holder.id_car_title.text = "${request.vehicle?.year} ${request.vehicle?.make} ${request.vehicle?.model}"
        holder.id_service_needed.text = request.service?.serviceType
        val address = request.clientInfo!!.address
        holder.id_address.text = "${address.street}\n${address.city}, ${address.state} ${address.zipCode}"

        var availability = request.clientInfo!!.availability
        var availabilityText = ""
        availability.days.forEach { day -> availabilityText += "$day " }
        availabilityText += "\n${availability.fromTime} to ${availability.toTime}"
        holder.id_availability.text = availabilityText
        holder.id_positive.setOnClickListener {
            acceptRequest()
        }

        holder.id_negative.setOnClickListener {
            createDeclineDialog()
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            MY_PERMISSION_REQ_GPS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mMap.isMyLocationEnabled = true
                    }
                    catch(ex: SecurityException) {
                        Log.d(TAG, ex.toString())
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun createDeclineDialog() {
        val container = layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val dialogBody = layoutInflater.inflate(R.layout.dialog_body_choice, null)
        val declineDialog = BasicDialog.Builder.apply{
            title = getString(R.string.text_decline)
            positive = getString(R.string.yes)
            negative = getString(R.string.text_cancel)
        }.build(this, container, dialogBody)
        declineDialog.show()

        declineDialog.id_positive.setOnClickListener {
            declineRequest()
            declineDialog.dismiss()
        }

        declineDialog.id_negative.setOnClickListener {
            declineDialog.dismiss()
        }
    }

    private fun acceptRequest() {
        val acceptedOn = System.currentTimeMillis()
        requestRef.document(request.objectID)
            .update("status", Status.Active,
                "acceptedOn", acceptedOn)
            ?.addOnSuccessListener {
                Toasty.makeText(this, "Request accepted", ToastyType.SUCCESS)
                finish()
            }.addOnFailureListener {
                Toasty.makeText(this, "Unable to accept request", ToastyType.FAIL)
            }
    }

    private fun declineRequest() {
        requestRef.document(request.objectID)
            .update("status", Status.Cancelled)
            ?.addOnSuccessListener {
                Toasty.makeText(this, "Request canceled", ToastyType.SUCCESS)
                finish()
            }.addOnFailureListener {
                Toasty.makeText(this, "Unable to cancel request", ToastyType.FAIL)
            }
    }

    override fun onResume() {
        AuthenticationManager.signInGuard(this)
        super.onResume()
    }
}
