package com.example.mobilemechanic.client

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
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.EditAccountInfoActivity
import com.example.mobilemechanic.MainActivity
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.findservice.FindServiceActivity
import com.example.mobilemechanic.client.garage.GarageActivity
import com.example.mobilemechanic.client.history.ClientHistoryActivity
import com.example.mobilemechanic.model.*
import com.example.mobilemechanic.shared.messaging.ChatRoomsActivity
import com.example.mobilemechanic.shared.signin.SignInActivity
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_client_welcome.*
import kotlinx.android.synthetic.main.content_client_frame.*

const val CLIENT_TAG = "client"

class ClientWelcomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var requestRef: CollectionReference

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var clientRequestRecyclerAdapter: ClientRequestRecyclerAdapter
    private var requests = ArrayList<Request>()
    private lateinit var mDrawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_welcome)
        setUpClientWelcomeActivity()
    }

    private fun setUpClientWelcomeActivity() {
        initFirestore()
        setUpToolBar()
        setUpDrawerMenu()
        setUpNavigationListener()
        setUpRequestRecyclerView()
        getUserAddress()
    }

    private fun initFirestore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection("Requests")
        Log.d(CLIENT_TAG, "[ClientWelcomeActivity] User uid: ${mAuth.currentUser?.uid}")
        Log.d(CLIENT_TAG, "[ClientWelcomeActivity] User email: ${mAuth.currentUser?.email}")
    }


    private fun setUpToolBar() {
        Log.d(CLIENT_TAG, "[ClientWelcomeActivity] user full otherMemberName ${mAuth?.currentUser?.displayName}")
        setSupportActionBar(id_welcome_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Welcome"
            subtitle = "${mAuth?.currentUser?.displayName}"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setUpDrawerMenu() {
        mDrawerLayout = findViewById(R.id.client_drawer_layout)
        val clientNavigationView = findViewById<NavigationView>(R.id.id_client_nav_view)
        val drawerHeader = clientNavigationView.getHeaderView(0)

        val drawerProfileImage = drawerHeader.findViewById<CircleImageView>(R.id.id_drawer_profile_image)
        var drawerDisplayName = drawerHeader.findViewById<TextView>(R.id.id_drawer_header_name)
        var drawerEmail = drawerHeader.findViewById<TextView>(R.id.id_drawer_header_email)

        drawerDisplayName.text = mAuth?.currentUser?.displayName
        drawerEmail.text = mAuth?.currentUser?.email
        displayProfileImage(drawerProfileImage)

        clientNavigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            true
        }
    }

    private fun displayProfileImage(drawerProfileImage: CircleImageView) {
        val userProfileUri = mAuth?.currentUser?.photoUrl
        Log.d(CLIENT_TAG, "[ClientWelcomeActivity] photoUrl ${mAuth?.currentUser?.photoUrl}")
        if (userProfileUri != null) {
            Picasso.get().load(userProfileUri).into(drawerProfileImage)
        } else {
            Picasso.get().load(R.drawable.ic_circle_profile).into(drawerProfileImage)
        }
    }

    private fun setUpNavigationListener() {
        id_client_nav_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.id_find_service -> {
                    startActivity(Intent(this, FindServiceActivity::class.java))
//                    startActivity(Intent(this, FindServiceActivityTest::class.java))
                    true
                }
                R.id.id_history -> {
                    startActivity(Intent(this, ClientHistoryActivity::class.java))
                    true
                }
                R.id.id_garage -> {
                    startActivity(Intent(this, GarageActivity::class.java))
                    true
                }
                R.id.id_messages ->{
                    val intent = Intent(this, ChatRoomsActivity::class.java)
                    //intent.putExtra(EXTRA_USER_TYPE, UserType.CLIENT.name)
                    startActivity(intent)
                    true
                }
                R.id.id_settings -> {
                    val intent = Intent(this, EditAccountInfoActivity::class.java)
                    intent.putExtra(EXTRA_USER_TYPE, UserType.CLIENT.name)
                    startActivity(intent)
                    true
                }
                R.id.id_sign_out -> {
                    mAuth?.signOut()
                    Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
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
        clientRequestRecyclerAdapter = ClientRequestRecyclerAdapter(this, requests)
        id_client_welcome_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = clientRequestRecyclerAdapter
        }
        reactiveRequestRecyclerView()
    }

    private fun reactiveRequestRecyclerView() {
        requestRef.whereEqualTo("clientInfo.uid", mAuth?.currentUser?.uid.toString())
            ?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                requests.clear()
                for (doc in querySnapshot!!) {
                    if (doc["status"] == Status.Request.name || doc["status"] == Status.Active.name) {
                        val request = doc.toObject(Request::class.java)
                        request.objectID = doc.id
                        Log.d(
                            CLIENT_TAG,
                            "[ClientWelcomeActivity]  snapshotListener ServiceModel documentID: ${request.objectID}"
                        )
                        requests.add(request)
                    }
                }
                clientRequestRecyclerAdapter.notifyDataSetChanged()
            }
    }

    private fun getUserAddress() {
        if (!AddressManager.hasAddress()) {
            mFirestore.collection("Accounts")
                .document(mAuth.currentUser?.uid.toString()).get()
                ?.addOnSuccessListener {
                    val user = it.toObject(User::class.java)
                    if (user != null) {
                        AddressManager.saveUserAddress(user.address)
                    }
                }
        }
    }

    private fun signInGuard() {
        Log.d(CLIENT_TAG, "[ClientWelcomeActivity] signInGuard() User uid: ${mAuth?.currentUser?.uid}")
        Log.d(CLIENT_TAG, "[ClientWelcomeActivity] User email: ${mAuth?.currentUser?.email}")
        val user = mAuth?.currentUser
        if (user == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
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
        signInGuard()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        setUpToolBar()
        super.onResume()
    }
}
