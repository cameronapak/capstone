package com.example.mobilemechanic.mechanic

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.messaging.MechanicChatRooms
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.model.adapter.RequestListAdapter
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mechanic_welcome.*
import kotlinx.android.synthetic.main.content_mechanic_frame.*

const val EXTRA_REQUEST = "service_request"
const val MECHANIC_TAG = "mechanic"

class MechanicWelcomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var mechanicRequestListAdapter: RequestListAdapter
    private var requests = ArrayList<Request>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_welcome)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection(getString(R.string.ref_requests))
        setUpMechanicWelcomeActivity()
    }

    private fun setUpMechanicWelcomeActivity() {
        mockLogin()       // Replace with real login later
        setUpToolBar()
        setUpDrawerMenu()
        setUpNavigationListener()
        setUpRequestRecyclerView()
    }

    private fun mockLogin() {
        mAuth?.signInWithEmailAndPassword("statham@gmail.com", "123456")
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = mAuth?.currentUser
                    Log.d(MECHANIC_TAG, "you is logged in ${user?.uid}")
                }
            }?.addOnFailureListener {
                Log.d(MECHANIC_TAG, it.toString())
            }
    }

    private fun setUpRequestRecyclerView() {
        viewManager = LinearLayoutManager(this)
        mechanicRequestListAdapter = RequestListAdapter(this, requests)
        id_mechanic_welcome_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mechanicRequestListAdapter
        }
        reactiveServiceRecyclerView()
    }

    private fun reactiveServiceRecyclerView() {
        requestRef.whereEqualTo("mechanicInfo.uid", mAuth?.currentUser?.uid.toString())
            ?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                requests.clear()
                for (doc in querySnapshot!!) {
                    if(doc["status"] == Status.Request.name || doc["status"] == Status.Active.name)
                    {
                        val request = doc.toObject(Request::class.java)
                        request.objectID = doc.id
                        Log.d(
                            MECHANIC_TAG,
                            "[MechanicServices] snapshotListener ServiceModel documentID: ${request.objectID}"
                        )
                        requests.add(request)
                    }
                }
                mechanicRequestListAdapter.notifyDataSetChanged()
            }
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_welcome_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Welcome"
            subtitle = "Mechanic: Jason Statham"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setUpDrawerMenu() {
        mDrawerLayout = findViewById(R.id.mechanic_drawer_layout)
        val clientNavigationView = findViewById<NavigationView>(R.id.id_mechanic_nav_view)
        clientNavigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            true
        }
    }

    private fun setUpNavigationListener() {
        id_mechanic_nav_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_notification -> {
                    // Handle the camera action
                    true
                }
                R.id.nav_services -> {
                    val i = Intent(this, MechanicServicesActivity::class.java)
                    startActivity(i)
                    true
                }
                R.id.nav_history -> {
                    true
                }
                R.id.nav_settings -> {
                    true
                }
                R.id.nav_share -> {
                    true
                }
                R.id.nav_messages -> {
                    val intent = Intent(this, MechanicChatRooms::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    mDrawerLayout.closeDrawers()
                    true
                }
            }
        }
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

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }
}
