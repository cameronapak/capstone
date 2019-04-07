package com.example.mobilemechanic.mechanic

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.adapter.ServiceListAdapter
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.example.mobilemechanic.model.dto.LatLngHolder
import com.example.mobilemechanic.model.dto.MechanicInfo
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mechanic_services.*
import kotlinx.android.synthetic.main.dialog_body_add_service.*
import kotlinx.android.synthetic.main.dialog_body_add_service.view.*
import kotlinx.android.synthetic.main.dialog_container_basic.*

class MechanicServicesActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var userAccountRef: DocumentReference
    private lateinit var serviceRef: CollectionReference
    private lateinit var reviewRef: CollectionReference

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var mechanicServiceAdapter: ServiceListAdapter
    private lateinit var basicDialog: Dialog
    private var services = ArrayList<ServiceModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_mechanic_services)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        userAccountRef = mFirestore?.collection("Accounts")
            ?.document(mAuth?.currentUser?.uid.toString())
        serviceRef = mFirestore?.collection("Services")
        reviewRef = mFirestore.collection("Reviews")

        Log.d(MECHANIC_TAG, "[MechanicServicesActivity] User uid: ${mAuth?.currentUser?.uid}")
        Log.d(MECHANIC_TAG, "[MechanicServicesActivity] User email: ${mAuth?.currentUser?.email}")
        setUpMechanicServiceActivity()
    }

    private fun setUpMechanicServiceActivity() {
        setUpToolBar()
        setUpServiceRecyclerView()
        setUpAddServiceDialog()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_manage_service_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Services"
            subtitle = "Manage your services"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpServiceRecyclerView() {
        viewManager = LinearLayoutManager(this)
        mechanicServiceAdapter = ServiceListAdapter(this, services)
        id_mechanic_service_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mechanicServiceAdapter
            isNestedScrollingEnabled = false
        }
        reactiveServiceRecyclerView()
    }

    private fun reactiveServiceRecyclerView() {
        serviceRef.whereEqualTo("mechanicInfo.uid", mAuth?.currentUser?.uid.toString())
            ?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                services.clear()
                for (doc in querySnapshot!!) {
                    val service = doc.toObject(ServiceModel::class.java)
                    service.objectID = doc.id
                    Log.d(MECHANIC_TAG, "[MechanicServices] snapshotListener ServiceModel documentID: ${service.objectID}")
                    services.add(service)
                }
                mechanicServiceAdapter.notifyDataSetChanged()
            }
    }

    private fun setUpAddServiceDialog() {
        id_add_service.setOnClickListener {
            val dialogContainer =
                layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)
            val dialogBody = layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_body_add_service, null)
            val service = DataProviderManager.getAllServices()
            dialogBody.add_service_spinner.adapter =
                HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, service, "Service")

            basicDialog = BasicDialog.Builder.apply {
                title = "Add Service"
                positive = "Add"
                negative = "Cancel"
            }.build(this, dialogContainer, dialogBody)

            basicDialog.show()
            handleDialogClicked(basicDialog)
        }
    }

    private fun handleDialogClicked(basicDialog: Dialog) {
        basicDialog.id_positive.setOnClickListener {
            val serviceType = basicDialog.add_service_spinner.selectedItem.toString().trim()
            val cost = basicDialog.label_price.text.toString().trim()
            val description = basicDialog.label_comment.text.toString().trim()
            val service = Service(serviceType, description, parseDouble(cost))

            if (isFieldsValidated(service)) {
                addService(service)
            } else {
                Toast.makeText(this, "Please enter in all fields", Toast.LENGTH_LONG).show()
            }
            basicDialog.dismiss()
        }

        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }
    }

    private fun addService(service: Service) {
        userAccountRef?.get()?.addOnSuccessListener {
            Log.d(MECHANIC_TAG, it.toString())
            val user = it.toObject(User::class.java)
            if (user != null) {
                Log.d(MECHANIC_TAG, "[MechanicServicesActivity] addServiceToAlgolia account $user")
                Log.d(MECHANIC_TAG, "[MechanicServicesActivity] addServiceToAlgolia service  $service")

                val address = user.address
                val mechanicAddressLatLng = AddressManager.convertAddressToLatLng(this, address)
                val latlngHolder = LatLngHolder(mechanicAddressLatLng.latitude, mechanicAddressLatLng.longitude)
                address._geoloc = latlngHolder

                val mechanicInfo = MechanicInfo(
                    user.uid,
                    user.basicInfo,
                    address,
                    user.rating
                )

                var service = ServiceModel("", mechanicInfo, service, latlngHolder, 0)
                getReviewCount(service)
                serviceRef.document().set(service)?.addOnSuccessListener { documentRef ->
                    Log.d(MECHANIC_TAG, "[MechanicServicesActivity] addServiceToAlgolia $documentRef")
                }
            }
        }
    }

    private fun getReviewCount(serviceModel: ServiceModel) {
        reviewRef.whereEqualTo("mechanicInfo", mAuth?.currentUser?.uid)
            .get()
            .addOnSuccessListener {
                serviceModel.reviewCount = it.size()
                saveServiceToFirestore(serviceModel)
            }
    }

    private fun saveServiceToFirestore(serviceModel: ServiceModel) {
        serviceRef.document().set(serviceModel)?.addOnSuccessListener { documentRef ->
            Log.d(MECHANIC_TAG, "[MechanicServicesActivity] addServiceToAlgolia $documentRef")
        }
    }

    private fun isFieldsValidated(service: Service): Boolean {
        if ((service.serviceType == "Services") ||
            service.price.toString().isNullOrEmpty() ||
            service.price.toString().isNullOrBlank() ||
            service.price == 0.0
        ) {
            return false
        }
        return true
    }

    private fun parseDouble(strNumber: String?): Double {
        return if (strNumber != null && strNumber.isNotEmpty()) {
            strNumber.toDouble()
        } else {
            0.0
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }
}
