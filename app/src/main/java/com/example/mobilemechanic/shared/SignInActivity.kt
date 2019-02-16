package com.example.mobilemechanic.shared

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()

        id_login_button.setOnClickListener { login() }

        id_forgot_password.setOnClickListener { resetPassword() }
    }
        private fun login() {
            val email = id_login_email.text.toString().trim()
            val password = id_login_password.text.toString().trim()

            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { Task ->
                    if (Task.isSuccessful) {
                        val i = Intent(this, ClientWelcomeActivity::class.java)

                        //dialog verification for clients
                        val dialogBuilder= AlertDialog.Builder(this)
                        dialogBuilder.setMessage(getString(R.string.email_identification, email))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes)) { _, _ ->// finish()
                                startActivity(i)
                                Toast.makeText(this, R.string.login_success,
                                    Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton(getString(R.string.no)) { _, _ -> //dialog.cancel()
                                val a = Intent(this, SignInActivity::class.java)
                                startActivity(a)
                            }
                        val alert =dialogBuilder.create()
                        alert.setTitle(getString(R.string.verification))
                        alert.show()

                    } else {
                        Toast.makeText(this, "Login Failed",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
        private fun resetPassword() {
            val i = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(i)
        }

    }

