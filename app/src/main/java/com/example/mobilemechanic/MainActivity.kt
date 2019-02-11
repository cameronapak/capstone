package com.example.mobilemechanic

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.shared.RegistrationActivity
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // For testing purposes
        navigationsSetup()
    }

    private fun navigationsSetup() {
        id_registrationButton.setOnClickListener(this)
        id_loginButton.setOnClickListener(this)
        id_clientButton.setOnClickListener(this)
        id_mechanicButton.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {

            // Yanfay
            R.id.id_registrationButton -> {
                startActivity(Intent(this, RegistrationActivity::class.java))
                true
            }

            // Bisi
            R.id.id_loginButton -> {


            }

            // Tomy, Dat, Cameron
            R.id.id_clientButton -> {
                startActivity(Intent(this, ClientWelcomeActivity::class.java))
                true
            }

            // Robert, Pham
            R.id.id_mechanicButton -> {
                val i = Intent(this, MechanicWelcomeActivity::class.java)
                startActivity(i)
                finish()

            }
        }
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

}
