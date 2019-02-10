package com.example.mobilemechanic.client

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.findservice.FindServiceActivity
import com.example.mobilemechanic.client.history.HistoryActivity
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.activity_client_welcome.*


class ClientWelcomeActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_welcome)
        setUpClientWelcomeActivity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun setUpClientWelcomeActivity() {
        setUpToolBar()
        setUpDrawerLayout()
        setUpNavigationListener()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_welcome_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Welcome"
            subtitle = "Jackie Chan"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setUpDrawerLayout() {
        mDrawerLayout = findViewById(R.id.client_drawer_layout)
        val clientNavigationView = findViewById<NavigationView>(R.id.id_client_nav_view)
        clientNavigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            true
        }
    }

    private fun setUpNavigationListener() {
        id_client_nav_view.setNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.id_service_request -> {
                    startActivity(Intent(this, FindServiceActivity::class.java))
                    Log.d("WELCOME", "Post service request selected")
                    true
                }
                R.id.id_history-> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    Log.d("History", "History activity selected")
                    true
                }
                else -> {
                    mDrawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }
}
