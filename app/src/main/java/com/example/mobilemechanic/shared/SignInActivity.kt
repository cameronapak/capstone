package com.example.mobilemechanic.shared

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        id_login_button.setOnClickListener { login() }
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
                    checkUserType()
                }
            }?.addOnFailureListener {
                Toast.makeText(this, "Unable to login", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkUserType() {
        mFirestore.collection("Accounts")
            .document(mAuth?.currentUser?.uid.toString()).get()
            ?.addOnSuccessListener {
                retrieveFcmToken()
                redirectToWelcomePageByUserType(it)
                val user = it.toObject(User::class.java)
                if (user?.userType == UserType.CLIENT) {
                    startActivity(Intent(this, ClientWelcomeActivity::class.java))
                } else {
                    startActivity(Intent(this, MechanicWelcomeActivity::class.java))
                }
            }
    }

    private fun redirectToWelcomePageByUserType(it: DocumentSnapshot) {
        val user = it.toObject(User::class.java)
        if (user?.userType == UserType.CLIENT) {
            startActivity(Intent(this, ClientWelcomeActivity::class.java))
        } else {
            startActivity(Intent(this, MechanicWelcomeActivity::class.java))
        }
    }

    private fun resetPassword() {
        val i = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(i)
    }

    private fun retrieveFcmToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(CLIENT_TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result?.token
                Log.d(CLIENT_TAG, "[SignInActvity] token: $token")

                sendFcmTokenToFirestore(token)
            })
    }

    private fun sendFcmTokenToFirestore(token: String?) {
        mFirestore = FirebaseFirestore.getInstance()
        Log.d(CLIENT_TAG, "[SignInActivity] sendFcmTokenToFirebtore() token: $token")
        Log.d(CLIENT_TAG, "[SignInActivity] mAuth.currentUser.email: ${mAuth.currentUser?.email}")
        Log.d(CLIENT_TAG, "[SignInActivity] mAuth.currentUser.email: ${mAuth.currentUser?.uid}")
        mFirestore.collection("Accounts")
            ?.document("${mAuth.currentUser?.uid}")
            .update("fcmToken", token)
            ?.addOnSuccessListener {
                Log.d(CLIENT_TAG, "[SignInActivity] update fcm token: $token to ${mAuth.currentUser?.email}")
            }?.addOnFailureListener {
                Log.d(CLIENT_TAG, "[SignInActivity] update fcm token Failed: ${it.message}")
            }
    }
}

