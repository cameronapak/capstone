package com.example.mobilemechanic.client.servicerating

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.*
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.history.ClientHistoryActivity
import com.example.mobilemechanic.model.Receipt
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Review
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_service_rating_activty.*

class ServiceRatingActivity : AppCompatActivity(), RatingBar.OnRatingBarChangeListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var reviewRef: CollectionReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_rating_activty)

        val ss: Request? = intent.getParcelableExtra("request")
        val question = findViewById<TextView>(R.id.id_rating_question)
        val ratingBar = findViewById<RatingBar>(R.id.id_rating_bar)
        val checkBox1 = findViewById<CheckBox>(R.id.id_rating_checkbox1)
        val checkBox2 = findViewById<CheckBox>(R.id.id_rating_checkbox2)
        val checkBox3 = findViewById<CheckBox>(R.id.id_rating_checkbox3)
        val checkBox4 = findViewById<CheckBox>(R.id.id_rating_checkbox4)
        val checkBox5 = findViewById<CheckBox>(R.id.id_rating_checkbox5)
        val checkBox6 = findViewById<CheckBox>(R.id.id_rating_checkbox6)
        val checkBox7 = findViewById<CheckBox>(R.id.id_rating_checkbox7)
        val submit = findViewById<Button>(R.id.id_rating_submit)
        var wrongText = ""

        var checkGroup = ArrayList<CheckBox>()

        checkGroup.add(checkBox1)
        checkGroup.add(checkBox2)
        checkGroup.add(checkBox3)
        checkGroup.add(checkBox4)
        checkGroup.add(checkBox5)
        checkGroup.add(checkBox6)
        checkGroup.add(checkBox7)

        question.text = "How was ${ss?.mechanicInfo?.basicInfo?.firstName}'s service?"
        ratingBar.numStars = 5
        ratingBar.stepSize = .5F
        ratingBar.onRatingBarChangeListener = this

        for(box in checkGroup) {
            box.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) box.setBackgroundColor(Color.CYAN)
                else {
                    box.setBackgroundColor(Color.WHITE)
                    box.setBackgroundResource(R.drawable.default_border)
                }


            }
        }

        submit.setOnClickListener {
            var s = ""

            for(box in checkGroup) {
                if(box.isChecked) wrongText += "${box.text} "
            }

            if(wrongText == "") {
                s = "Name: ${ss?.mechanicInfo?.rating} \nRating: ${id_rating_rate.text} \nComment: ${id_rating_comment.text}"
            }else {
                s = "Name: ${ss?.mechanicInfo?.rating} \nRating: ${id_rating_rate.text} \nTags: $wrongText \nComment: ${id_rating_comment.text}"
            }

            val review = Review(ss?.clientInfo, ss?.mechanicInfo, id_rating_comment.text.toString(), wrongText, id_rating_rate.text.toString().toFloat())

            Toast.makeText(applicationContext, s, Toast.LENGTH_LONG).show()
            wrongText = ""

            addRating(review)
        }

        setUpServiceRatingActivity()

    }

    private fun addRating(review: Review) {
        try {
            reviewRef.document()?.set(review)?.addOnSuccessListener { documentRef ->
            }
        }catch (e: Throwable) {
            reviewRef.whereEqualTo("clientInfo.uid",review.clientInfo?.uid)
                .whereEqualTo("mechanicInfo.uid", review.mechanicInfo?.uid)
                ?.addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }
                    for (doc in querySnapshot!!) {
                        reviewRef?.document(doc.id).set(
                            review
                        )

                    }
                }
        }

//        val intent = Intent(applicationContext, ClientHistoryActivity::class.java)
//        applicationContext.startActivity(intent)


    }


    private fun setUpServiceRatingActivity() {
        setUpToolBar()
        initFireStore()
        mockLogin()
    }

    //mock login
    private fun mockLogin() {
        mAuth?.signInWithEmailAndPassword("dat@gmail.com", "123456")
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = mAuth?.currentUser
                    Log.d("Rating", "Login ID: ${user?.uid}")
                }
            }?.addOnFailureListener {
                Log.d("Rating", it.toString())
            }
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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        id_rating_rate.text = "$rating"
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

}


