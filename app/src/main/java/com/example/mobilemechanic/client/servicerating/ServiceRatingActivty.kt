package com.example.mobilemechanic.client.servicerating

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RatingBar
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.activity_service_rating_activty.*

class ServiceRatingActivity : AppCompatActivity(), RatingBar.OnRatingBarChangeListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_rating_activty)

        val ss:String = intent.getStringExtra("name")
        val question = findViewById<TextView>(R.id.id_rating_how)
        val ratingBar = findViewById<RatingBar>(R.id.id_rating_bar)

        question.text = "How was ${ss}'s service?"
        ratingBar.numStars = 5
        ratingBar.stepSize = .5F
        ratingBar.onRatingBarChangeListener = this
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        id_rating_rate.text = "$rating"
    }
    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }
}
