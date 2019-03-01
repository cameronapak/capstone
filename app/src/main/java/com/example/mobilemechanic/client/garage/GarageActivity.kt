package com.example.mobilemechanic.client.garage

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.utility.ScreenManager
import kotlinx.android.synthetic.main.activity_garage.*

class GarageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garage)
        setUpGarageActivity()

        id_add_vehicle.setOnClickListener {
            val i = Intent(this, AddVechicleInfo::class.java)
            startActivity(i)
        }
    }

    private fun setUpGarageActivity() {
        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_garage_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Garage"
            subtitle = "Manage your vehicles"
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
