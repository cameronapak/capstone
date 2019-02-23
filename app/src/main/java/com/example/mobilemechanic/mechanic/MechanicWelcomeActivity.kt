package com.example.mobilemechanic.mechanic

import android.app.Dialog
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
import android.view.View
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.model.Vehicle
import com.example.mobilemechanic.model.adapter.RequestListAdapter
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mechanic_welcome.*
import kotlinx.android.synthetic.main.dialog_container_basic.view.*
import kotlinx.android.synthetic.main.content_mechanic_frame.*

const val EXTRA_REQUEST = "service_request"
const val REQ_CODE_MORE_INFO = 1
const val MECHANIC_TAG = "mechanic"
class MechanicWelcomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var mechanicRequestListAdapter: RequestListAdapter
    private var requests = ArrayList<Request>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_welcome)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        setUpMechanicWelcomeActivity()
    }

    private fun setUpMechanicWelcomeActivity() {
       //mockLogin()       // Replace with real login later
        mockRequests()    // Replace with real requests later
        setUpToolBar()
        setUpDrawerMenu()
        setUpNavigationListener()
        setUpRequestRecyclerView()
    }

    private fun mockLogin() {
        mAuth?.signInWithEmailAndPassword("datm@gmail.com", "123456")
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = mAuth?.currentUser
                    Log.d(MECHANIC_TAG, "you is logged in ${user?.uid}")
                }
            }?.addOnFailureListener {
                Log.d(MECHANIC_TAG, it.toString())
            }
    }

    private fun mockRequests() {
        for (i in 0..10) {
            val mockService = Service("Oil Change", 20.00, "")
            val mockRequest = Request(
                "12345", "12345", "Need an oil change!",
                Vehicle(), mockService, Status.Request, System.currentTimeMillis(), 0L
            )
            requests.add(mockRequest)
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
                R.id.nav_send -> {
                    true
                }
                else -> {
                    mDrawerLayout.closeDrawers()
                    true
                }
            }
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

    fun createChoiceDialog(title: String): Dialog {
        var positive = ""
        var dialogBody: View? = null
        if (title == getString(R.string.label_choice_accept)) {
            positive = getString(R.string.yes)
            dialogBody = layoutInflater.inflate(R.layout.dialog_body_choice, null)
        } else if (title == getString(R.string.label_choice_complete)) {
            positive = getString(R.string.label_choice_confirm)
            dialogBody = layoutInflater.inflate(R.layout.dialog_body_complete, null)
        }

        val dialogContainer = layoutInflater.inflate(com.example.mobilemechanic.R.layout.dialog_container_basic, null)

        var choiceDialog = BasicDialog.Builder.apply {
            this.title = title
            this.positive = positive
            negative = getString(R.string.label_cancel_add_service)
        }.build(this, dialogContainer, dialogBody!!)


        dialogContainer.id_positive.setOnClickListener {
            Toast.makeText(this, "You pressed Positive!", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "${choiceDialog.bundle}", Toast.LENGTH_LONG).show()
            choiceDialog.dismiss()
        }

        dialogContainer.id_negative.setOnClickListener {
            Toast.makeText(this, "You pressed Negative!", Toast.LENGTH_SHORT).show()
            choiceDialog.dismiss()
        }

        return choiceDialog
    }

    override fun onResume() {
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }
}
