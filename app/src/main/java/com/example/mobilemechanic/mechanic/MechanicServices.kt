package com.example.mobilemechanic.mechanic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.addservice.AddService
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.adapter.ServiceListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_service.*
import kotlinx.android.synthetic.main.activity_mechanic_services.*
import kotlinx.android.synthetic.main.content_mechanic_services.*
import kotlinx.android.synthetic.main.service_card_view.*


class MechanicServices : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var serviceArray = ArrayList<Service>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_services)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


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
                val i = Intent(this, AddService::class.java)
                startActivity(i)

return true

            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
