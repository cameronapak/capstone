package com.example.mobilemechanic.client.findservice

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.ServiceModel
import com.example.mobilemechanic.shared.ScreenManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_find_service.*

class FindServiceActivity : AppCompatActivity() {

    private lateinit var services: ArrayList<ServiceModel>
    private lateinit var mFireStore: FirebaseStorage
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var serviceAdapter: ServiceRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_service)

        mFireStore = FirebaseStorage.getInstance()

        services = ArrayList()

        val mockService =
            ServiceModel(
                "Jason Statham",
                "jasonuid",
                "Oil Change",
                30.toDouble(),
                "Guarantee complete under 30 minutes.",
                4.5f
            )


        services.add(mockService)

        viewManager = LinearLayoutManager(this)
        serviceAdapter = ServiceRecyclerAdapter(this, services)
        id_recyclerview_services.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = serviceAdapter
        }

        serviceAdapter.notifyDataSetChanged()
        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_find_service_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
