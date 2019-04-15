package com.example.mobilemechanic.mechanic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.EditAccountInfoActivity
import com.example.mobilemechanic.MainActivity
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.mechanic.history.MechanicHistoryActivity
import com.example.mobilemechanic.model.*
import com.example.mobilemechanic.model.adapter.RequestListAdapter
import com.example.mobilemechanic.shared.Toasty
import com.example.mobilemechanic.shared.ToastyType
import com.example.mobilemechanic.shared.messaging.ChatRoomsActivity
import com.example.mobilemechanic.shared.registration.fragments.ACCOUNT_DOC_PATH
import com.example.mobilemechanic.shared.signin.SignInActivity
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
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
    
    private var selectedImageUri: Uri? = null
    private lateinit var emptyView: LinearLayout
    private var isFirstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_welcome)
        emptyView = findViewById<LinearLayout>(R.id.id_empty_state_view)
        setUpMechanicWelcomeActivity()
    }

    private fun setUpMechanicWelcomeActivity() {
        initFirestore()
        setUpToolBar()
        setUpDrawerMenu()
        setUpNavigationListener()
        setUpRequestRecyclerView()
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
                    if(doc["status"] == Status.Request.name || doc["status"] == Status.Active.name || doc["status"] == Status.Paid.name)
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

                // toggle empty state view
                if (requests.isNullOrEmpty()) {
                    Log.d(
                        MECHANIC_TAG,
                        "[MechanicServices] Requests are empty. Showing empty state view."
                    )
                    emptyView.setVisibility(View.VISIBLE)
                } else {
                    emptyView.setVisibility(View.GONE)
                }

                mechanicRequestListAdapter.notifyDataSetChanged()
                if(isFirstLoad)
                {
                    ScreenManager.toggleVisibility(id_progress_bar)
                    isFirstLoad = false
                }
            }
    }

    private fun initFirestore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        requestRef = mFirestore.collection(getString(R.string.ref_requests))
        Log.d(MECHANIC_TAG, "[MechanicWelcomeActivity] User uid: ${mAuth.currentUser?.uid}")
        Log.d(MECHANIC_TAG, "[MechanicWelcomeActivity] User email: ${mAuth.currentUser?.email}")
    }

    private fun setUpToolBar() {
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
        mDrawerLayout = findViewById(R.id.mechanic_drawer_layout)
        val mechanicNavigationView = findViewById<NavigationView>(R.id.id_mechanic_nav_view)
        val drawerHeader = mechanicNavigationView.getHeaderView(0)
        val drawerProfileImage = drawerHeader.findViewById<CircleImageView>(R.id.id_drawer_profile_image)
        var drawerDisplayName = drawerHeader.findViewById<TextView>(R.id.id_drawer_header_name)
        var drawerEmail = drawerHeader.findViewById<TextView>(R.id.id_drawer_header_email)

        drawerDisplayName.text = mAuth?.currentUser?.displayName
        drawerEmail.text = mAuth?.currentUser?.email

        updateName()
        displayProfileImage(drawerProfileImage)
        editProfileImageOption(drawerProfileImage)

        mechanicNavigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            true
        }
    }

    private fun displayProfileImage(drawerProfileImage: CircleImageView) {
        val userProfileUri = mAuth?.currentUser?.photoUrl
        Log.d(CLIENT_TAG, "[MechanicWelcomeActivity] photoUrl ${mAuth?.currentUser?.photoUrl}")
        if (userProfileUri != null) {
            Picasso.get().load(userProfileUri).into(drawerProfileImage)
        } else {
            Picasso.get().load(R.drawable.ic_circle_profile).into(drawerProfileImage)
        }
    }

    private fun editProfileImageOption(drawerProfileImage: CircleImageView) {
        drawerProfileImage.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setFixAspectRatio(true)
            .setAspectRatio(1, 1)
            .start(this)
    }

    private fun updateName() {
        mFirestore.collection(ACCOUNT_DOC_PATH).document(mAuth.uid.toString()).get()
            .addOnSuccessListener {
                val userInfo = it.toObject(User::class.java)
                val drawerDisplayName = findViewById<TextView>(R.id.id_drawer_header_name)
                val drawerEmail = findViewById<TextView>(R.id.id_drawer_header_email)

                drawerDisplayName.text = "${userInfo!!.basicInfo.firstName} ${userInfo!!.basicInfo.lastName}"
                drawerEmail.text = "${userInfo!!.basicInfo.email}"
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                val resultUri = result.uri
                showSelectedProfileImage(resultUri)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSelectedProfileImage(imageUri: Uri) {
        if (imageUri != null) {
            val mechanicNavigationView = findViewById<NavigationView>(R.id.id_mechanic_nav_view)
            val drawerHeader = mechanicNavigationView.getHeaderView(0)
            val drawerProfileImage = drawerHeader.findViewById<CircleImageView>(R.id.id_drawer_profile_image)
            if (drawerProfileImage != null) {
                drawerProfileImage.setImageDrawable(null)
                selectedImageUri = imageUri
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                drawerProfileImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun setUpNavigationListener() {
        id_mechanic_nav_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_messages -> {
                    val intent = Intent(this, ChatRoomsActivity::class.java)
                    intent.putExtra(EXTRA_USER_TYPE, UserType.MECHANIC.name)
                    startActivity(intent)
                    true
                }
                R.id.nav_services -> {
                    startActivity(Intent(this, MechanicServicesActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, MechanicHistoryActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, EditAccountInfoActivity::class.java)
                    intent.putExtra(EXTRA_USER_TYPE, UserType.MECHANIC.name)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.id_sign_out -> {
                    mAuth.signOut()
                    Toasty.makeText(this, "Logged out", ToastyType.SUCCESS)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signInGuard() {
        val user = mAuth?.currentUser
        if (user == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        signInGuard()
        setUpToolBar()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
        super.onResume()
    }
}
