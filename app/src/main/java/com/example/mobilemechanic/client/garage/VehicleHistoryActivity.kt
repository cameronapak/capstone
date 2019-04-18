package com.example.mobilemechanic.client.garage

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.model.Vehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_vehicle_history.*
import kotlinx.android.synthetic.main.card_vehicle_basic_container.*

class VehicleHistoryActivity : AppCompatActivity() {

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var requestRef: CollectionReference

    private lateinit var historyAdapter: VehicleHistoryRecyclerAdapter
    private lateinit var vehicle: Vehicle
    private var requestReceipt: ArrayList<Request> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_history)
        setUpVehicleHistoryActivity()
    }

    private fun setUpVehicleHistoryActivity() {
        initFirestore()
        getVehicleParcelable()
        setUpToolBar()
        setUpVehicleCard()
        setUpAdapter()
        setUpVehicleHistoryRecylerView()
    }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        requestRef = mFirestore.collection("Requests")

    }

    private fun getVehicleParcelable() {
        vehicle = intent.getParcelableExtra(EXTRA_VEHICLE)
        Log.d(CLIENT_TAG, "[VehicleHistoryActivity] $vehicle")

    }

    private fun setUpToolBar() {
        setSupportActionBar(id_vehicle_history_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Vehicle History"
            subtitle = "Previous services completed for $vehicle"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpVehicleCard() {
        id_vehicle_title.text = "$vehicle"
        val vehicleImage = findViewById<ImageView>(R.id.id_vehicle_image)
        if (!vehicle.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(Uri.parse(vehicle.photoUrl)).into(vehicleImage)
        }
    }

    private fun setUpAdapter() {
        val viewManager = LinearLayoutManager(this)
        historyAdapter = VehicleHistoryRecyclerAdapter(this, requestReceipt)
        id_vehicle_history_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = historyAdapter
            addItemDecoration(DividerItemDecoration(id_vehicle_history_recyclerview.context, viewManager.orientation))
        }
        historyAdapter.notifyDataSetChanged()
    }

    private fun setUpVehicleHistoryRecylerView() {
        requestRef.whereEqualTo("vehicle.objectID", vehicle.objectID)
            .whereEqualTo("status", Status.Completed)
            .orderBy("completedOn", Query.Direction.DESCENDING)
            ?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                requestReceipt.clear()
                for (doc in querySnapshot!!) {
                    val request = doc.toObject(Request::class.java)
                    request.objectID = doc.id
                    requestReceipt.add(request)
                }
                historyAdapter.notifyDataSetChanged()
            }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
