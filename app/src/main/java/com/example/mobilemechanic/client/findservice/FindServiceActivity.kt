package com.example.mobilemechanic.client.findservice

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.ServiceModel
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_find_service.*

class FindServiceActivity : AppCompatActivity() {


    private lateinit var mFireStore: FirebaseStorage
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var serviceAdapter: ServiceRecyclerAdapter
    private var services: ArrayList<ServiceModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_service)
        mFireStore = FirebaseStorage.getInstance()
        setUpFindServiceActivity()
    }

    private fun setUpFindServiceActivity() {
        setUpAlgolioa()
        setUpToolBar()
    }

    private fun setUpAlgolioa() {
        viewManager = LinearLayoutManager(this)
        serviceAdapter = ServiceRecyclerAdapter(this, services)
        id_recyclerview_services.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = serviceAdapter
        }
        serviceAdapter.notifyDataSetChanged()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_find_service_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
