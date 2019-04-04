package com.example.mobilemechanic.client.mechanicreview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.findservice.EXTRA_DISTANCE
import com.example.mobilemechanic.client.findservice.EXTRA_MECHANIC_INFO
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.Review
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.adapter.ReviewListAdapter
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.example.mobilemechanic.model.dto.MechanicInfo
import com.example.mobilemechanic.shared.messaging.ChatRoomsActivity
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_mechanic_reviews.*
import kotlinx.android.synthetic.main.content_mechanic_reviews.*
import kotlinx.android.synthetic.main.content_mechanic_reviews.view.*

class MechanicReviewsActivity : AppCompatActivity() {

    private lateinit var mechanicInfo: MechanicInfo
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var serviceRef: CollectionReference
    private lateinit var reviewRef: CollectionReference

    private var reviewsList: ArrayList<Review> = ArrayList()
    private var serviceItems: ArrayList<ServiceModel> = ArrayList()

    private lateinit var reviewsAdapter: ReviewListAdapter

    val REQUEST_PHONE_CALL = 1
    val REQUEST_EMAIL = 2
    var count: Int = 0
    var distance = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_reviews)
        setUpActivity()
    }

    private fun setUpActivity() {
        parseParcelable()
        setUpFirestore()
        setUpAdapter()
        setUpButtons()
        getMechanicServices()
        getReviews()
        setUpToolBar()
    }

    private fun setUpFirestore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        serviceRef = mFirestore.collection("Services")
        reviewRef = mFirestore.collection("Reviews")
    }

    private fun parseParcelable() {
        mechanicInfo = intent.getParcelableExtra(EXTRA_MECHANIC_INFO)
        distance = intent.getStringExtra(EXTRA_DISTANCE.toString())
    }

    private fun getMechanicServices() {
        serviceRef.get()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    for(document in it.result!!) {
                        val service = document.toObject(ServiceModel::class.java)
                        if(service.mechanicInfo.uid == mechanicInfo.uid) {
                            Log.d(CLIENT_TAG, "[MechanicReviewsActivity] ObjectID: ${document.id}")
                            serviceItems.add(service)
                        }

                    }
                }
            }
    }

    private fun getReviews() {

        count = 0
        reviewRef.get()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    for(document in it.result!!) {
                        val review = document.toObject(Review::class.java)

                        if(review.mechanicInfo?.uid == mechanicInfo.uid) {
                            Log.d(CLIENT_TAG, "[MechanicReviewsActivity] ObjectID: ${document.id}")
                            count++
                            reviewsList.add(review)
                        }
                    }
                    showMechanicInfo()

                }
            }
    }

    private fun showMechanicInfo() {
        val holder = id_mechanic_info_card
        if(mechanicInfo.basicInfo.photoUrl != null) {
            Picasso.get().load(mechanicInfo.basicInfo.photoUrl).into(holder.id_mechanic_more_info_image)
        }else {
            Picasso.get().load(R.drawable.ic_circle_profile).into(holder.id_mechanic_more_info_image)
        }

        holder.id_mechanic_more_info_name.text = "${mechanicInfo?.basicInfo?.firstName} ${mechanicInfo?.basicInfo?.lastName}"
        holder.id_mechanic_more_info_rating.rating = mechanicInfo.rating
        holder.id_mechanic_more_info_reviews_count.text = "$count Reviews"
        holder.id_mechanic_more_info_distance.text = distance
        holder.id_mechanic_more_info_service_items.text = ""
        for(item in serviceItems) {
            if(!holder.id_mechanic_more_info_service_items.text.contains(item.service.serviceType))
            holder.id_mechanic_more_info_service_items.text = "${holder.id_mechanic_more_info_service_items.text} ${item.service.serviceType}\n"
        }
    }

    private fun setUpAdapter() {
        val viewManager = LinearLayoutManager(this)
        reviewsAdapter = ReviewListAdapter(this, reviewsList)
        id_recyclerview_reviews.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = reviewsAdapter
        }
        reviewsAdapter.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        when(requestCode)
        {
            REQUEST_PHONE_CALL->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startCall()
                else//request permission
                //context, constant for access call, permission request code
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                        REQUEST_PHONE_CALL)
            }
        }
    }

    private fun setUpButtons() {

        id_email_button.setOnClickListener {
        val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(mechanicInfo.basicInfo.email))
            startActivity(intent)
        }

        id_call_button.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
            {
                startCall()
            }
            else//request permission
            {
                //context, constant for access call, permission request code
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_PHONE_CALL)
            }
        }

        id_message_button.setOnClickListener {
            val intent = Intent(this, ChatRoomsActivity::class.java)
            intent.putExtra(EXTRA_USER_TYPE, UserType.CLIENT.name)
            startActivity(intent)
            true
        }
    }

    @SuppressLint("MissingPermission")
    fun startCall(){
        val phoneNum = mechanicInfo?.basicInfo?.phoneNumber
            ?.replace("[^0-9\\+]".toRegex(), "")

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNum"))
        startActivity(intent)
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_mechanic_reviews_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Reviews"
            subtitle = "See what others think about ${mechanicInfo.basicInfo.firstName}"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

}
