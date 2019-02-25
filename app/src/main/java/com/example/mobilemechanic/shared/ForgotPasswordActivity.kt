package com.example.mobilemechanic.shared

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.mobilemechanic.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

const val TEST = "test"
class ForgotPasswordActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()

        id_pwd_reset.setOnClickListener {
            val email = id_pwd_email.text.toString().trim()
            Log.d(TEST, "[ForgotPasswordActivity]: $email")

            if (email.isNullOrEmpty() || email.isBlank()) {
                Toast.makeText(this, "Please enter in a valid email.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener(this) {

                if (it.isSuccessful) {
                    Toast.makeText(this, R.string.reset_success, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
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

