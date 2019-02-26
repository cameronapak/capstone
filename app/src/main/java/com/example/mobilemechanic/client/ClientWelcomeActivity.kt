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
import android.widget.Toast
import com.example.mobilemechanic.MainActivity
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.findservice.FindServiceActivity
import com.example.mobilemechanic.client.garage.GarageActivity
import com.example.mobilemechanic.client.history.ClientHistoryActivity
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_client_welcome.*

const val CLIENT_TAG = "client"
class ClientWelcomeActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    var mAuth: FirebaseAuth? = null

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
        setUpDrawerMenu()
        setUpNavigationListener()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_welcome_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Welcome"
            subtitle = "Client: Jackie Chan"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setUpDrawerMenu() {
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
                R.id.id_find_service -> {
                    startActivity(Intent(this, FindServiceActivity::class.java))
                    Log.d("WELCOME", "Post service request selected")
                    true
                }
                R.id.id_history -> {
                    startActivity(Intent(this, ClientHistoryActivity::class.java))
                    Log.d("History", "History activity selected")
                    true
                }
                R.id.id_garage -> {
                    startActivity(Intent(this, GarageActivity::class.java))
                    true
                }
                R.id.id_sign_out -> {
                    mAuth?.signOut()
                    Toast.makeText(this,"Logged Out",Toast.LENGTH_SHORT ).show()
                    val i = Intent(this, MainActivity::class.java )
                    startActivity(i)
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
