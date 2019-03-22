package com.example.mobilemechanic.client.review

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.findservice.EXTRA_MECHANIC_INFO
import com.example.mobilemechanic.model.dto.MechanicInfo
import com.example.mobilemechanic.shared.utility.ScreenManager
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : AppCompatActivity() {

    private lateinit var mechanicInfo: MechanicInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        setUpReviewActivity()
    }

    private fun setUpReviewActivity() {
        getMechanicInfoParcelable()
        setUpActionBar()
    }

    private fun getMechanicInfoParcelable() {
        mechanicInfo = intent.getParcelableExtra(EXTRA_MECHANIC_INFO)
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_review_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Reviews"
            subtitle = "See what others think about  ${mechanicInfo.basicInfo.firstName}"
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
