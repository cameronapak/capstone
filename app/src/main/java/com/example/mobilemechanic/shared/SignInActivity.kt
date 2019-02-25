package com.example.mobilemechanic.shared

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        id_login_button.setOnClickListener {login()}
        id_forgot_password.setOnClickListener { resetPassword() }
    }
        private fun login() {
            val email = id_login_email.text.toString().trim()
            val password = id_login_password.text.toString().trim()
            if (email.isEmpty() or password.isEmpty()) {
                Toast.makeText(this, "Please enter in email and password.", Toast.LENGTH_LONG).show()
                return
            }

            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        mFirestore.collection("Accounts")
                            .document(mAuth?.currentUser?.uid.toString()).get()
                            ?.addOnSuccessListener {
                                val user = it.toObject(User::class.java)
                                if (user?.userType == UserType.CLIENT) {
                                    startActivity(Intent(this, ClientWelcomeActivity::class.java))
                                } else {
                                    startActivity(Intent(this, MechanicWelcomeActivity::class.java))
                                }
                            }
                    } else {
                        Toast.makeText(this, "Login Failed",
                            Toast.LENGTH_SHORT).show()
                    }
                }?.addOnFailureListener {
                    Toast.makeText(this, "Unable to login", Toast.LENGTH_LONG).show()
                }
        }
        private fun resetPassword() {
            val i = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(i)
        }

    }

