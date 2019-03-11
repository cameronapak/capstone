package com.example.mobilemechanic.client.detail

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
import com.example.mobilemechanic.mechanic.map.MY_PERMISSION_REQ_GPS
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_service_detail.*
import kotlinx.android.synthetic.main.client_card_vehicle_container.view.*
import kotlinx.android.synthetic.main.dialog_body_contact.*
import kotlinx.android.synthetic.main.dialog_container_basic.*

class ServiceDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var request: Request
    private lateinit var mMap: GoogleMap
    private lateinit var basicDialog: Dialog

    val REQUEST_PHONE_CALL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)
        setUpServiceDetailActivity()
    }

    private fun setUpServiceDetailActivity() {
        getRequestParcel()
        setUpVehicleContainer()
        setUpMap()
        setUpToolBar()
    }

    private fun getRequestParcel() {
        request = intent.getParcelableExtra(EXTRA_REQUEST)
    }

    private fun setUpVehicleContainer()
    {
        val holder = id_client_more_info_card
        holder.id_client_car_title.text = "${request.vehicle?.year} ${request.vehicle?.make} ${request.vehicle?.model}"
        holder.id_service_completed.text = request.service?.serviceType
        val address = request.clientInfo!!.address
        holder.id_client_address.text = "${address.street}\n${address.city}, ${address.state} ${address.zipCode}"
        holder.id_summary.text = getSummary()
        holder.id_mechanic.text = "${request.mechanicInfo!!.basicInfo.firstName} ${request.mechanicInfo!!.basicInfo.lastName}"

        holder.id_contact.setOnClickListener {
            setUpContactServiceDialog()
        }
    }

    private fun getSummary(): String {
        var subTotal = java.lang.String.format("%-15s %15s", "Subtotal", request.receipt?.subTotal)
        var estimatedTax = java.lang.String.format("%-15s %15s", "Estimated tax", request.receipt?.estimatedTax)
        var grandTotal = java.lang.String.format("%-15s %15s", "Grand total", request.receipt?.grandTottal)

        var summary = "${subTotal}\n${estimatedTax}\n\n${grandTotal}"

        return  summary
    }

    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
                MY_PERMISSION_REQ_GPS
            )
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
                        Log.d(com.example.mobilemechanic.mechanic.map.TAG, ex.toString())
                    }
                }
            }

            REQUEST_PHONE_CALL->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startCall()
                else//request permission
                    //context, constant for access call, permission request code
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                        REQUEST_PHONE_CALL)
            }
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
            negative = "Message"
        }.build(this, dialogContainer, dialogBody)

        val basicInfo = request.mechanicInfo!!.basicInfo

        val showPhone= basicInfo.phoneNumber
        val showName = "${basicInfo.firstName}  ${basicInfo.lastName}"
        basicDialog.id_phone_number.text = showPhone
        basicDialog.id_show_client_name.text = showName

        basicDialog.show()

        basicDialog.id_positive.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
            {
                startCall()
            }
            else//request permission
            {
                //context, constant for access call, permission request code
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_PHONE_CALL)
            }
        }
        basicDialog.id_negative.setOnClickListener {

        }
    }

    @SuppressLint("MissingPermission")
    fun startCall(){
        val phoneNum = request.mechanicInfo?.basicInfo?.phoneNumber
            ?.replace("[^0-9\\+]".toRegex(), "")

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum))
        startActivity(intent)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }
}
