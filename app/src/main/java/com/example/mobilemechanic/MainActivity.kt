package com.example.mobilemechanic

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MECHANIC_TAG
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.shared.RegistrationActivity
import com.example.mobilemechanic.shared.ScreenManager
import com.example.mobilemechanic.shared.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // For testing purposes
        navigationsSetup()

        mAuth = FirebaseAuth.getInstance()

        id_login_button.setOnClickListener {

            Log.d(MECHANIC_TAG, "[MainActivity]: onCreate() login clicked")
            mAuth?.signInWithEmailAndPassword("dat@gmail.com", "Password1!")
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(MECHANIC_TAG, "[MainActivity]: logged in success")
                    }

                    val uid = mAuth?.currentUser?.uid
                    Log.d(MECHANIC_TAG, "[MainActivity]: ${mAuth?.currentUser?.uid}")
                }
        }
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

            }

            // Bisi
            R.id.id_loginButton -> {
                val i = Intent(this, SignInActivity::class.java)
                startActivity(i)

            }

            // Tomy, Dat, Cameron
            R.id.id_clientButton -> {
                startActivity(Intent(this, ClientWelcomeActivity::class.java))

            }

            // Robert, Pham
            R.id.id_mechanicButton -> {
                val i = Intent(this, MechanicWelcomeActivity::class.java)
                startActivity(i)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }

}
