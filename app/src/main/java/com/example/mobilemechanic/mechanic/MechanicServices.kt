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
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mechanic_services.*
import kotlinx.android.synthetic.main.dialog_body_add_service.*
import kotlinx.android.synthetic.main.dialog_body_add_service.view.*
import kotlinx.android.synthetic.main.dialog_container_basic.*


class MechanicServices : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var userAccountRef: DocumentReference

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var mechanicServiceAdapter: ServiceListAdapter
    private lateinit var basicDialog: Dialog
    private var services = ArrayList<Service>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_mechanic_services)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        userAccountRef = mFirestore?.collection("Accounts")
            ?.document(mAuth?.currentUser?.uid.toString())

        Log.d(MECHANIC_TAG, "[MechanicServices] User uid: ${mAuth?.currentUser?.uid}")
        Log.d(MECHANIC_TAG, "[MechanicServices] User email: ${mAuth?.currentUser?.email}")
        setUpMechanicServiceActivity()
    }

    private fun setUpMechanicServiceActivity() {
        setUpToolBar()
        setUpServiceRecyclerView()
        setUpAddService()
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
        }
        populateServiceRecyclerView()
        reactiveServiceRecyclerView()
    }

    private fun populateServiceRecyclerView() {
        userAccountRef?.get()?.addOnSuccessListener { it ->
            Log.d(MECHANIC_TAG, it.toString())
            val account = it.toObject(User::class.java)
            if (account?.services != null) {
                services.clear()
                account?.services?.forEach {
                    services.add(it)
                    Log.d(MECHANIC_TAG, "services: ${account?.services}\n")
                }
            }
            mechanicServiceAdapter.notifyDataSetChanged()
        }?.addOnFailureListener {
            Log.d(MECHANIC_TAG, it.message.toString())
        }
    }

    private fun reactiveServiceRecyclerView() {
        userAccountRef?.addSnapshotListener { snapshot, exception ->
            val account = snapshot?.toObject(User::class.java)
            if (account?.services != null) {
                services.clear()
                account?.services?.forEach {
                    services.add(it)
                    Log.d(MECHANIC_TAG, "[MechanicServices] addSnapshotListener: $it")
                }
            }
            mechanicServiceAdapter.notifyDataSetChanged()
        }
    }

    private fun setUpAddService() {
        id_add_service.setOnClickListener {
            val dialogContainer =
                layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)
            val dialogBody = layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_body_add_service, null)
            val service = DataProviderManager.getAllServices()
            dialogBody.add_service_spinner.adapter =
                HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, service)
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
            val comment = basicDialog.label_comment.text.toString().trim()
            val newService = Service(serviceType, parseDouble(cost), comment)

            if (isFieldsValidated(newService)) {
                addService(newService)
            } else {
                Toast.makeText(this, "Please enter in all fields", Toast.LENGTH_LONG).show()
            }
            basicDialog.dismiss()
        }

        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }
    }

    private fun addService(newService: Service) {
        userAccountRef?.update("services", FieldValue.arrayUnion(newService))
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Service Added Successfully", Toast.LENGTH_LONG).show()
                    Log.d(MECHANIC_TAG, "[MechanicServices] addService: successful")
                }
            }?.addOnFailureListener {
                Log.d(MECHANIC_TAG, "[MechanicServices] ${it.message}")
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
