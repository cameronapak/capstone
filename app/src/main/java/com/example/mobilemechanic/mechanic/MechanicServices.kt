package com.example.mobilemechanic.mechanic

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.R

import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.adapter.ServiceListAdapter
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_service.*
import kotlinx.android.synthetic.main.activity_add_service.view.*
import kotlinx.android.synthetic.main.activity_mechanic_services.*
import kotlinx.android.synthetic.main.basic_dialog.view.*
import kotlinx.android.synthetic.main.content_mechanic_services.*

const val log = "TAG"

class MechanicServices : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var serviceArray = ArrayList<Service>()
    private var services = ArrayList<Service>()
    private lateinit var basicDialog: Dialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_services)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


val test = 0;

        //Recycler View
        id_mechanic_service_recyclerview.layoutManager = LinearLayoutManager(this)
        id_mechanic_service_recyclerview.adapter = ServiceListAdapter(this, serviceArray)
        val change = id_mechanic_service_recyclerview.adapter
        change?.notifyDataSetChanged()

        //update info on card view
        db?.collection("Accounts")?.document(mAuth?.uid.toString())
            ?.get()?.addOnSuccessListener {
                val Serv = it.toObject(User::class.java)
                if(Serv!!.services != null){
                    serviceArray.clear()
                    Serv!!.services!!.forEach {
                        serviceArray.add(it)
                    }
                    change?.notifyDataSetChanged()
                }

        }

        Log.d(log, "${mAuth?.currentUser?.uid.toString()}")
        db?.collection("Accounts")
            ?.document(mAuth?.uid.toString())
            ?.addSnapshotListener { snapshot, e ->

                if (snapshot != null && snapshot.exists()) {
                    Log.d(log, "Current data: " + snapshot.data)

                    val Serv = snapshot.toObject(User::class.java)

                    if (Serv != null) {
                        services.clear()
                        Serv?.services?.forEach {
                            services.add(it)
                            Log.d(log, "$it\n")
                        }

                    }

                    id_mechanic_service_recyclerview.adapter?.notifyDataSetChanged()
                } else {
                    Log.d(log, "Current data: null")
                }
            }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mechanic_service, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_add -> {
                val dialogContainer
                        = layoutInflater.inflate(com.example.mobilemechanic.R.layout.basic_dialog, null)
                // inflate your custom body here.
                val dialogBody
                        = layoutInflater.inflate(com.example.mobilemechanic.R.layout.activity_add_service, null)

                val service = DataProviderManager.getAllServices()
                dialogBody.add_service_spinner.adapter =
                    HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, service)


               //dialogBody.add_service_spinner.onItemSelectedListener = this

                basicDialog = BasicDialog.Builder.apply {
                    title = "Add Service"
                    positive = "Add"
                    negative = "Cancel"
                }.build(this, dialogContainer,dialogBody)

                basicDialog.show()
                    handleDialogClicked(basicDialog, dialogContainer, dialogBody)
                //Log.d(log, services.toString())
return true

            }
            else -> return super.onOptionsItemSelected(item)
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

    private fun addService(){
        //val serviceType = findViewById<Spinner>(R.id.add_service_spinner).selectedItem.toString().trim()
        val serviceType = basicDialog.add_service_spinner.selectedItem.toString().trim()
        val cost = basicDialog.label_price.text.toString().trim()
        val comment = basicDialog.label_comment.text.toString().trim()

        if(serviceType.isNullOrEmpty() || serviceType.isNullOrBlank()) {
            Toast.makeText(this, "Invalid service type", Toast.LENGTH_SHORT).show()
            return
        }

        if(cost.isNullOrEmpty() || cost.isNullOrBlank()) {
            Toast.makeText(this, "Invalid service type", Toast.LENGTH_SHORT).show()
            return
        }

        if(comment.isNullOrEmpty() || comment.isNullOrBlank()) {
            Toast.makeText(this, "Invalid service type", Toast.LENGTH_SHORT).show()
            return
        }
        val user = mAuth?.currentUser?.email.toString()

        services.clear()
        services.add(Service(serviceType, cost.toDouble(), comment))


        db?.collection("Accounts")?.document(mAuth?.uid.toString())
            ?.get()?.addOnSuccessListener {
                val Serv = it.toObject(User::class.java)

                if(Serv!!.services == null){
                    db?.collection("Accounts")
                        ?.document(mAuth?.uid.toString())
                        ?.update("services", services )?.addOnSuccessListener {
                            val adapter = id_mechanic_service_recyclerview.adapter
                            adapter?.notifyDataSetChanged()
                            //Log.d(log, services.toString())
                        }
                }
                else {
                    val delService = Serv!!.services!!.find { s -> s.serviceType == serviceType}
                    if( delService != null){
                        Toast.makeText(this, "You can't have same service", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Serv!!.services!!.forEach {
                            services.add(it)
                            //Log.d(log, services.toString())
                        }
                        db?.collection("Accounts")
                            ?.document(mAuth?.uid.toString())
                            ?.update("services", services)?.addOnCanceledListener {
                                //Log.d(log, services.toString())
                                val adapter = id_mechanic_service_recyclerview.adapter
                                adapter?.notifyDataSetChanged()
                            }

                    }
                }
                val adapter = id_mechanic_service_recyclerview.adapter
                adapter?.notifyDataSetChanged()
            }
            ?.addOnFailureListener { ex: Exception ->
                Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show()
            }
        val adapter = id_mechanic_service_recyclerview.adapter
        adapter?.notifyDataSetChanged()
    }

}
