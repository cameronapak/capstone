package com.example.mobilemechanic.mechanic

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.adapter.RequestListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mechanic_welcome.*
import kotlinx.android.synthetic.main.app_bar_mechanic_welcome.*
import kotlinx.android.synthetic.main.content_mechanic_welcome.*

class MechanicWelcomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var requests = ArrayList<Request>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_welcome)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //Add placeholder data to recycler view for now...
        for(i in 0..10)
        {
            requests.add(Request())
        }

        //Recycler View
        id_mechanic_welcome_recyclerview.layoutManager = LinearLayoutManager(this)
        id_mechanic_welcome_recyclerview.adapter = RequestListAdapter(this, requests)
        id_mechanic_welcome_recyclerview.adapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mechanic_welcome, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_notification -> {
                // Handle the camera action
            }
            R.id.nav_services -> {
                val i = Intent(this, MechanicServices::class.java)
                startActivity(i)
                //finish()
            }
            R.id.nav_history -> {

            }
            R.id.nav_settings -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
