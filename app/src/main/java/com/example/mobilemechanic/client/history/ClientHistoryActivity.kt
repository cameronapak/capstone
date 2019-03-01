package com.example.mobilemechanic.client.history

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.*
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.example.mobilemechanic.model.dto.*
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_client_history.*


class ClientHistoryActivity : AppCompatActivity() {

    private lateinit var receipts: ArrayList<Receipt>

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference

    private lateinit var historyAdapter: ClientHistoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_history)
        initFireStore()
//        login()
        mockLogin()
        setUpReceipt()
//        mockClient()
        setUpAdapter()
        setUpToolBar()
    }

    private fun setUpReceipt() {

        receipts = ArrayList()

        requestRef.whereEqualTo("clientInfo.uid", mAuth?.currentUser?.uid.toString())
            ?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                receipts.clear()
                for (doc in querySnapshot!!) {
                    val request = doc.toObject(Request::class.java)
                    val receipt = Receipt("receiptID", request, 30.0, 1.5, 31.5)

                    receipts.add(receipt)
                }
                    historyAdapter.notifyDataSetChanged()
            }
    }

    private fun mockLogin() {
        mAuth?.signInWithEmailAndPassword("dat@gmail.com", "123456")
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = mAuth?.currentUser
                    Log.d("history", "Login ID: ${user?.uid}")
                }
            }?.addOnFailureListener {
                Log.d("history", it.toString())
            }
    }

    private fun login() {
//        mAuth?.signInWithEmailAndPassword( )
//            ?.addOnCompleteListener {
//                if (it.isSuccessful) {
//                    val user = mAuth?.currentUser
//                    Log.d("history", "Login ID: ${user?.uid}")
//                }
//            }?.addOnFailureListener {
//                Log.d("history", it.toString())
    }

    private fun mockClient() {
        receipts = ArrayList()

        // mocked data

        // create client information
        val availableDays = arrayListOf("mon", "tues")
        val clientBasicInfo = BasicInfo("Jackie", "Chan", "jackie@gmail.com", "1231231234", "")
        val clientAddress = Address("119 Somestreet", "Edmond", "OK", "73161")
        val clientAvailability = Availability("9:00 AM", "5:00 PM", availableDays)
        val clientInfo = ClientInfo("clientID", clientBasicInfo, clientAvailability, clientAddress)


        // create mechanic information
        val mechanicBasicInfo = BasicInfo("Jason", "Statham", "statham@gmail.com", "3123213214", "")
        val mechanicAddress = Address("666 Chick road", "Boston", "MA", "02125")
        val mechanicInfo = MechanicInfo("mechanicID", mechanicBasicInfo, mechanicAddress, 0f)

        // create service
        val service = Service("Oil Change", "quick and fast", 30.0)

        // create service that include mechanic info
        val serviceModel = ServiceModel("serviceID", mechanicInfo, service)

        // create vehicle
        val vehicle = Vehicle("vehicleID", "2011", "Toyota", "Venza", "vehiclePhotoUrl")

        val currentTime = System.currentTimeMillis() / 1000


        val request = Request.Builder()
            .clientInfo(clientInfo)
            .mechanicInfo(mechanicInfo)
            .service(serviceModel.service)
            .vehicle(vehicle)
            .comment("Client comment")
            .status(Status.Request)
            .postedOn(currentTime)
            .acceptedOn(-1)
            .completedOn(-1)
            .build()

        val mockedReceipt = Receipt("receiptID", request, 30.0, 1.5, 31.5)

        receipts.add(mockedReceipt)
        receipts.add(mockedReceipt)
        receipts.add(mockedReceipt)
    }

    private fun initFireStore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection("Requests")
    }

    private fun setUpAdapter() {
        val viewManager = LinearLayoutManager(this)
        historyAdapter = ClientHistoryRecyclerAdapter(this, receipts)
        id_recyclerview_history.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = historyAdapter
        }

        historyAdapter.notifyDataSetChanged()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_client_history_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "History"
            subtitle = "Previous services"
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
