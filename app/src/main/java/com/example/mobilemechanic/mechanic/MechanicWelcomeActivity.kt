package com.example.mobilemechanic.mechanic

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.adapter.RequestListAdapter
import com.example.mobilemechanic.shared.BasicDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_mechanic_welcome.*
import kotlinx.android.synthetic.main.app_bar_mechanic_welcome.*
import kotlinx.android.synthetic.main.basic_dialog.view.*
import kotlinx.android.synthetic.main.content_mechanic_welcome.*

const val EXTRA_REQUEST = "service_request"
const val REQ_CODE_MORE_INFO = 1
const val MECHANIC_TAG = "mechanic"
class MechanicWelcomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null

    private var requests = ArrayList<Request>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_welcome)
        setSupportActionBar(toolbar)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        Log.d(MECHANIC_TAG, "[MechanicWelcomeActivity]: onCreate()")
        Log.d(MECHANIC_TAG, mAuth.toString())
//        login for testing
        mAuth?.signInWithEmailAndPassword("dpham9@uco.edu", "123456")
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = mAuth?.currentUser
                    Log.d(MECHANIC_TAG, "you is logged in ${user?.uid}")
                }
            }?.addOnFailureListener {
                Log.d(MECHANIC_TAG, it.toString())
            }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            mAuth?.signInWithEmailAndPassword("testdb2@uco.edu", "123456")
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("tag", "you is logged in")
                    }
                }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //Add placeholder data to recycler view for now...
        for (i in 0..10) {
            requests.add(Request())
        }
        //load test requests from firebase
        /*
        db?.collection("Requests/${mAuth?.currentUser?.email}/requests")
            ?.document(Timestamp.now().toString())
            ?.set(Request(mAuth!!.currentUser!!.uid!!.toString(), ))
            */

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
        menuInflater.inflate(R.menu.mechanic_welcome, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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

        val dialogContainer = layoutInflater.inflate(com.example.mobilemechanic.R.layout.basic_dialog, null)

        var choiceDialog = BasicDialog.Builder.apply {
            this.title = title
            this.positive = positive
            negative = getString(R.string.label_cancel_add_service)
        }.build(this, dialogContainer, dialogBody!!)


        dialogContainer.id_positive.setOnClickListener {
            Toast.makeText(this, "You pressed Positive!", Toast.LENGTH_SHORT).show()
            choiceDialog.dismiss()
        }

        dialogContainer.id_negative.setOnClickListener {
            Toast.makeText(this, "You pressed Negative!", Toast.LENGTH_SHORT).show()
            choiceDialog.dismiss()
        }

        return choiceDialog
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth?.currentUser
    }
}
