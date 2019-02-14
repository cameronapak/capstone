package com.example.mobilemechanic.shared

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mobilemechanic.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()

        id_pwd_reset.setOnClickListener {
            val email = id_pwd_email.text.toString().trim()

            mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener(this) {

               // Toast.makeText(this, R.string.email_input, Toast.LENGTH_SHORT).show()

                if (it.isSuccessful) {
                    Toast.makeText(this, R.string.reset_success, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.reset_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }

        id_pwd_cancel.setOnClickListener {
            val i= Intent(this, SignInActivity::class.java)
            startActivity(i)
        }
    }
}

