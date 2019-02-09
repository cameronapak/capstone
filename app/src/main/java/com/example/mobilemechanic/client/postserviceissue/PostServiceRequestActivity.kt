package com.example.mobilemechanic.client.postserviceissue

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_post_service_request.*


class PostServiceRequestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_post_service_request)
        setUpPostServiceRequestActivity()
    }

    private fun setUpPostServiceRequestActivity() {
        setUpToolBar()
        setUpFormSpinners()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_service_form_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpFormSpinners() {
        setUpVehicleSpinner()
        setUpServiceSpinner()
    }

    private fun setUpVehicleSpinner() {
        val vehicles =
            arrayOf("Select your Vehicle","2011 Toyota Venza", "2013 Toyota Camry")
                .asList()


        checkIfClientGarageEmpty()
        id_vehicle_spinner.adapter =
            HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, vehicles)
    }

    private fun setUpServiceSpinner() {
        val services =
            arrayOf("Select a service", "Oil Change", "Change tire", "Air tire", "Check Engine Light").
                asList()

        id_service_spinner.adapter =
            HintSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, services)
    }

    private fun checkIfClientGarageEmpty() {

    }

    override fun onResume() {
        super.onResume()
        hideStatusBar()
        hideBottomNavigationBar()
        hideWarningIconAndMessage()
    }

    private fun hideStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun hideBottomNavigationBar() {
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun hideWarningIconAndMessage() {
        id_warning_icon.visibility = View.GONE
        id_warning_message.visibility = View.GONE
    }
}
