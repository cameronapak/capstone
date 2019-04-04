package com.example.mobilemechanic.client.servicerating

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.history.EXTRA_REQUEST_RATING
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Review
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.example.mobilemechanic.shared.utility.NumberManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_service_rating.*

class ServiceRatingActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var reviewRef: CollectionReference
    private lateinit var requestRef: CollectionReference
    private lateinit var serviceRef: CollectionReference
    private lateinit var accountRef: CollectionReference

    private lateinit var request: Request
    private var checkBoxesOfWhatWentWrong = ArrayList<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_rating)
        setUpServiceRatingActivity()
    }

    private fun setUpServiceRatingActivity() {
        getRequestParcel()
        setUpToolBar()
        initFireStore()
        setUpReview()
        hideKeyboard()
    }

    private fun getRequestParcel() {
        request = intent.getParcelableExtra(EXTRA_REQUEST_RATING)
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_rating_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Rating"
            subtitle = "Rate the service"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initFireStore() {
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        reviewRef = mFirestore.collection("Reviews")
        requestRef = mFirestore.collection("Requests")
        serviceRef = mFirestore.collection("Services")
        accountRef = mFirestore.collection("Accounts")
    }

    private fun setUpReview() {
        setUpRatingBar()
        setUpCheckBoxes()
        handleOnSubmit()
    }

    private fun setUpRatingBar() {
        id_rating_question.text = "How was ${request?.mechanicInfo?.basicInfo?.firstName}'s service?"
        id_rating_bar.setOnRatingBarChangeListener { ratingBar, _, _ ->
            id_rating_text.text = ratingBar.rating.toString()
        }
    }

    private fun setUpCheckBoxes() {
        checkBoxGroup()
    }

    private fun handleOnSubmit() {
        id_rating_submit.setOnClickListener {
            val listOfWhatWentWrong = ArrayList<String>()
            checkBoxesOfWhatWentWrong.forEach { checkbox ->
                if (checkbox.isChecked) {
                    listOfWhatWentWrong.add(checkbox.text.toString())
                }
            }

            val review = Review(
                "",
                NumberManager.round(id_rating_bar.rating, 2),
                id_rating_comment.text.toString(),
                listOfWhatWentWrong,
                DateTimeManager.currentTimeMillis(),
                request.objectID,
                request.mechanicInfo
            )

            addReview(review)
            Log.d(CLIENT_TAG, "[ServiceRatingActivity] $listOfWhatWentWrong")
            Log.d(CLIENT_TAG, "[ServiceRatingActivity] requestID ${request?.objectID}")
        }
    }

    private fun checkBoxGroup() {
        val checkBox1 = findViewById<CheckBox>(R.id.id_rating_checkbox1)
        val checkBox2 = findViewById<CheckBox>(R.id.id_rating_checkbox2)
        val checkBox3 = findViewById<CheckBox>(R.id.id_rating_checkbox3)
        val checkBox4 = findViewById<CheckBox>(R.id.id_rating_checkbox4)
        val checkBox5 = findViewById<CheckBox>(R.id.id_rating_checkbox5)
        val checkBox6 = findViewById<CheckBox>(R.id.id_rating_checkbox6)
        val checkBox7 = findViewById<CheckBox>(R.id.id_rating_checkbox7)

        checkBoxesOfWhatWentWrong.add(checkBox1)
        checkBoxesOfWhatWentWrong.add(checkBox2)
        checkBoxesOfWhatWentWrong.add(checkBox3)
        checkBoxesOfWhatWentWrong.add(checkBox4)
        checkBoxesOfWhatWentWrong.add(checkBox5)
        checkBoxesOfWhatWentWrong.add(checkBox6)
        checkBoxesOfWhatWentWrong.add(checkBox7)
    }

    private fun addReview(review: Review) {
        reviewRef.document(review.requestID).set(review)
            .addOnSuccessListener {
                Log.d(CLIENT_TAG, "[ServiceRatingActivity] review added")
                Toast.makeText(this, "Review submitted", Toast.LENGTH_LONG).show()
            }.addOnCompleteListener {
                if(it.isSuccessful) {
                    calcMechanicRating(review)
                }
                finish()
            }
    }

    private fun calcMechanicRating(review: Review) {
        var avgRating = 0.00F
        var reviewDocs = ArrayList<String>()

        reviewRef.whereEqualTo("mechanicInfo.uid", review.mechanicInfo?.uid).get()
            .addOnSuccessListener {
                var totalRating = 0F
                var count = 0

                for(document in it) {
                    Log.d(CLIENT_TAG, "[ServiceRatingActivity] Review ID: ${document.id}")
                    count++
                    val item = document.toObject(Review::class.java)
                    totalRating += item.rating
                    reviewDocs.add(item.requestID)
                }

                avgRating = totalRating/count
                Log.d(CLIENT_TAG, "[ServiceRatingActivity] Average Rating: ${avgRating}")

            }.addOnCompleteListener {
                if(it.isSuccessful) {
                    populateRatings(avgRating, review.mechanicInfo?.uid!!)
                }
            }
    }

    private fun populateRatings(avgRating: Float, mechanicID: String) {

        reviewRef.whereEqualTo("mechanicInfo.uid", mechanicID).get()
            .addOnCompleteListener {
                val batch = mFirestore.batch()

                if(it.isSuccessful) {
                    for(item in it.result!!) {
                        val path = reviewRef.document(item.id)
                        batch.update(path,"mechanicInfo.rating", avgRating)
                    }
                }
                batch.commit()
            }

        requestRef.whereEqualTo("mechanicInfo.uid", mechanicID).get()
            .addOnCompleteListener {
                val batch = mFirestore.batch()

                if(it.isSuccessful) {
                    for(item in it.result!!) {
                        val path = requestRef.document(item.id)
                        batch.update(path,"mechanicInfo.rating", avgRating)
                    }
                }
                batch.commit()
            }

        serviceRef.whereEqualTo("mechanicInfo.uid", mechanicID).get()
            .addOnCompleteListener {
                val batch = mFirestore.batch()

                if(it.isSuccessful) {
                    for(item in it.result!!) {
                        val path = serviceRef.document(item.id)
                        batch.update(path,"mechanicInfo.rating", avgRating)
                    }
                }
                batch.commit()
            }

        accountRef.whereEqualTo("uid", mechanicID).get()
            .addOnCompleteListener {
                val batch = mFirestore.batch()

                if(it.isSuccessful) {
                    for(item in it.result!!) {
                        val path = accountRef.document(item.id)
                        batch.update(path,"rating", avgRating)
                    }
                }
                batch.commit()
            }
    }


    private fun hideKeyboard() {
        id_service_rating_framelayout.setOnClickListener {
            ScreenManager.hideKeyBoard(this)
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


