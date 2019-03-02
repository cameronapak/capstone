package com.example.mobilemechanic.client.detail

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import kotlinx.android.synthetic.main.activity_service_detail.*

class ServiceDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_detail)

        setUpServiceDetailActivity()
    }

    private fun setUpServiceDetailActivity() {
        setUpToolBar()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_service_detail_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Service Details"
            subtitle = "Service on <service-date>"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
