package com.example.mobilemechanic.client.findservice

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.ServiceModel
import com.example.mobilemechanic.shared.ScreenManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_find_service.*

class FindServiceActivity : AppCompatActivity() {

    private lateinit var services: ArrayList<ServiceModel>

    private lateinit var mFireStore: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_service)

        mFireStore = FirebaseStorage.getInstance()
        
        services = ArrayList<ServiceModel>()

        val mockService =
            ServiceModel("Jason Statham", "jasonuid", "Oil Change", 30.toDouble(), "description", 4.5f)


        services.add(mockService)

        val viewManager = LinearLayoutManager(this)
        val serviceAdapter = ServiceRecyclerAdapter(this, services)
        id_recyclerview_services.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = serviceAdapter
        }

        serviceAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }
}
