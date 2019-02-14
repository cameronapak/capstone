package com.example.mobilemechanic.mechanic.addservice

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_service.*


class AddService : Activity() {

    private var mAuth: FirebaseAuth?= null
    private var mDb: FirebaseFirestore?= null

    private var services = ArrayList<Service>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service)

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width*.8).toInt(), (height*.7).toInt())

        val param = window.attributes
        param.gravity = Gravity.CENTER
        param.x =0
        param.y = -20

        window.attributes = param

        val service = DataProviderManager.getAllServices()
        add_service_spinner.adapter =
            HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, service)

        label_cancel_add_service.setOnClickListener {
            finish()
        }

        label_add_service.setOnClickListener {
            addService()
        }
    }

    private fun addService(){
        val serviceType = findViewById<Spinner>(R.id.add_service_spinner).selectedItem.toString().trim()
        val cost = label_price.text.toString().trim()
        val comment = label_comment.text.toString().trim()

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

        mDb?.collection("Accounts")?.document(mAuth?.uid.toString())
            ?.get()?.addOnSuccessListener {
                val Serv = it.toObject(User::class.java)
                if(Serv!!.services == null){
                    mDb?.collection("Accounts")
                        ?.document(mAuth?.uid.toString())
                        ?.update("services", services )
                    val i = Intent()
                    setResult(Activity.RESULT_OK, i)
                    finish()
                }
                else {
                    val delService = Serv!!.services!!.find { s -> s.serviceType == serviceType}
                    if( delService != null){
                        Toast.makeText(this, "You can't have same service", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Serv!!.services!!.forEach {
                            services.add(it)
                        }
                        mDb?.collection("Accounts")
                            ?.document(mAuth?.uid.toString())
                            ?.update("services", services)
                        finish()
                    }
                }
            }
            ?.addOnFailureListener { ex: Exception ->
                Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show()
            }
    }
}
