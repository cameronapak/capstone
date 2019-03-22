package com.example.mobilemechanic.client.mechanicreview

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.utility.ScreenManager

import kotlinx.android.synthetic.main.activity_mechanic_reviews.*

class MechanicReviewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_reviews)
        setUpActivity()
    }

    private fun setUpActivity() {
        setUpToolBar()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_mechanic_reviews_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Reviews"
            subtitle = "See what others think about Jason"
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
