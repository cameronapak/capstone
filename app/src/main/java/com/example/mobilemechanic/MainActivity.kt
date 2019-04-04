package com.example.mobilemechanic

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.shared.Toasty
import com.example.mobilemechanic.shared.ToastyType
import com.example.mobilemechanic.shared.registration.RegistrationActivity
import com.example.mobilemechanic.shared.signin.SignInActivity
import com.example.mobilemechanic.shared.utility.ScreenManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigationsSetup()
    }

    private fun navigationsSetup() {
        id_registrationButton.setOnClickListener(this)
        id_loginButton.setOnClickListener(this)
        id_clientButton.setOnClickListener(this)
        id_mechanicButton.setOnClickListener(this)
        id_toast_success.setOnClickListener(this)
        id_toast_fail.setOnClickListener(this)
        id_toast_warning.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.id_registrationButton -> {
                startActivity(Intent(this, RegistrationActivity::class.java))

            }
            R.id.id_loginButton -> {
                startActivity(Intent(this, SignInActivity::class.java))

            }
            R.id.id_clientButton -> {
                startActivity(Intent(this, ClientWelcomeActivity::class.java))

            }
            R.id.id_mechanicButton -> {
                startActivity(Intent(this, MechanicWelcomeActivity::class.java))
            }
            R.id.id_toast_success -> {
                Toasty.makeText(this, "Success", ToastyType.SUCCESS)
            }
            R.id.id_toast_fail -> {
                Toasty.makeText(this, "Fail", ToastyType.FAIL)
            }
            R.id.id_toast_warning -> {
                Toasty.makeText(this, "Warning", ToastyType.WARNING)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

}
