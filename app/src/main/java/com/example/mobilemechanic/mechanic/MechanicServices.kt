package com.example.mobilemechanic.mechanic

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.adapter.ServiceListAdapter
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_service.*
import kotlinx.android.synthetic.main.activity_mechanic_services.*
import kotlinx.android.synthetic.main.dialog_container_basic.view.*

class MechanicServices : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var mechanicServiceAdapter: ServiceListAdapter
    private lateinit var basicDialog: Dialog
    private var services = ArrayList<Service>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_services)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        Log.d(MECHANIC_TAG, "[MechanicServices] User uid: ${mAuth?.currentUser?.uid}")
        Log.d(MECHANIC_TAG, "[MechanicServices] User email: ${mAuth?.currentUser?.email}")
        setUpMechanicServiceActivity()
    }

    private fun setUpMechanicServiceActivity() {
        setUpToolBar()
        setUpServiceRecyclerView()
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

    //  "zCi7WKObkrcjmqOL3IR7wl64ZZM2"
    private fun populateServiceRecyclerView() {
        mFirestore?.collection("Accounts")
            ?.document(mAuth?.currentUser?.uid.toString())
            ?.get()?.addOnSuccessListener { it ->
                Log.d(MECHANIC_TAG, it.toString())
                val account = it.toObject(User::class.java)
                if (account?.services != null) {
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
        mFirestore?.collection("Accounts")
            ?.document(mAuth?.currentUser?.uid.toString())
            ?.addSnapshotListener { snapshot, exception ->
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

    private fun handleDialogClicked(basicDialog: Dialog, dialogContainer: View, dialogBody: View) {
        dialogContainer.id_positive.setOnClickListener {
            addService()
            basicDialog.dismiss()
        }

        dialogContainer.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }
    }

    private fun addService() {
        val serviceType = basicDialog.add_service_spinner.selectedItem.toString().trim()
        val cost = basicDialog.label_price.text.toString().trim()
        val comment = basicDialog.label_comment.text.toString().trim()

        if (serviceType.isNullOrEmpty() || serviceType.isNullOrBlank()) {
            Toast.makeText(this, "Invalid service type", Toast.LENGTH_SHORT).show()
            return
        }

        if (cost.isNullOrEmpty() || cost.isNullOrBlank()) {
            Toast.makeText(this, "Invalid service type", Toast.LENGTH_SHORT).show()
            return
        }

        if (comment.isNullOrEmpty() || comment.isNullOrBlank()) {
            Toast.makeText(this, "Invalid service type", Toast.LENGTH_SHORT).show()
            return
        }


        services.clear()
        services.add(Service(serviceType, cost.toDouble(), comment))

        mFirestore?.collection("Accounts")?.document(mAuth?.currentUser?.email.toString())
            ?.get()?.addOnSuccessListener {
                val Serv = it.toObject(User::class.java)

                if (Serv!!.services == null) {
                    mFirestore?.collection("Accounts")
                        ?.document(mAuth?.currentUser?.email.toString())
                        ?.update("services", services)
                } else {
                    val delService = Serv!!.services!!.find { s -> s.serviceType == serviceType }
                    if (delService != null) {

                        Toast.makeText(this, "You can't have same service", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    } else {
                        Serv!!.services!!.forEach {
                            services.add(it)
                            //Log.d(log, services.toString())
                        }
                        mFirestore?.collection("Accounts")
                            ?.document(mAuth?.currentUser?.email.toString())
                            ?.update("services", services)
                        //Log.d(log, "Add again ${it}")

                    }
                }
            }
            ?.addOnFailureListener { ex: Exception ->
                Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show()
            }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.mechanic_service, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_add -> {
//                val dialogContainer = layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)
//                // inflate your custom body here.
//                val dialogBody = layoutInflater.inflate(com.example.mobilemechanic.R.layout.activity_add_service, null)
//
//                val service = DataProviderManager.getAllServices()
//                dialogBody.add_service_spinner.adapter =
//                    HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, service)
//                basicDialog = BasicDialog.Builder.apply {
//                    title = "Add Service"
//                    positive = "Add"
//                    negative = "Cancel"
//                }.build(this, dialogContainer, dialogBody)
//
//                basicDialog.show()
//                handleDialogClicked(basicDialog, dialogContainer, dialogBody)
//                //Log.d(log, services.toString())
//                return true
//
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }
}
