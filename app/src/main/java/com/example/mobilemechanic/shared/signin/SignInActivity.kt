package com.example.mobilemechanic.shared.signin

import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
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
import com.example.mobilemechanic.shared.BasicDialog
import com.example.mobilemechanic.shared.registration.RegistrationActivity
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.dialog_body_reset_password.*
import kotlinx.android.synthetic.main.dialog_container_basic.*

const val USER_TAG = "user"
class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var basicDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        setUpSignInActivity()
    }

    private fun setUpSignInActivity() {
        id_login_button.setOnClickListener { login() }
        id_forgot_password.setOnClickListener { resetPassword() }
        id_get_started.setOnClickListener { startRegistrationActivity() }
        enableHideKeyboard()
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
                val user = it.toObject(User::class.java)
                if (user != null) {
                    updateUserProfile(user)
                }
            }
    }


    private fun updateUserProfile(userInfo: User) {
        Log.d(CLIENT_TAG, "[SignInActivity] AddressManager.saveUserAddress ${userInfo.address}")
        AddressManager.saveUserAddress(userInfo.address)

        val user = mAuth?.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("${userInfo.basicInfo.firstName} ${userInfo.basicInfo.lastName}")
                .setPhotoUri(Uri.parse("${userInfo.basicInfo.photoUrl}"))
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(CLIENT_TAG, "[SignInActivity] updateUserProfile completed")
                        Log.d(CLIENT_TAG, "[SignInActivity] photoUri ${userInfo.basicInfo.photoUrl}")
                    }

                    if (userInfo.userType == UserType.CLIENT) {
                        startActivity(Intent(this, ClientWelcomeActivity::class.java))
                    } else {
                        startActivity(Intent(this, MechanicWelcomeActivity::class.java))
                    }
                    finish()
                }
        }
    }

    private fun resetPassword() {
        val container = layoutInflater.inflate(R.layout.dialog_container_basic, null)
        val body = layoutInflater.inflate(R.layout.dialog_body_reset_password, null)

        basicDialog = BasicDialog.Builder.build(this, container, body)
        basicDialog.show()

        handleDialogOnClick(basicDialog)
    }

    private fun retrieveFcmToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(CLIENT_TAG, "[SignInActivity] getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result?.token
                Log.d(CLIENT_TAG, "[SignInActivity] token: $token")

                sendFcmTokenToFirestore(token)
            })
    }

    private fun sendFcmTokenToFirestore(token: String?) {
        mFirestore = FirebaseFirestore.getInstance()
        Log.d(CLIENT_TAG, "[SignInActivity] sendFcmTokenToFirebtore() token: $token")
        mFirestore.collection("Accounts")
            .document("${mAuth.currentUser?.uid}")
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d(CLIENT_TAG, "[SignInActivity] update fcm token: $token to ${mAuth.currentUser?.email}")
            }.addOnFailureListener {
                Log.d(CLIENT_TAG, "[SignInActivity] update fcm token Failed: ${it.message}")
            }
    }

    private fun startRegistrationActivity() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    private fun handleDialogOnClick(basicDialog: Dialog) {
        basicDialog.id_negative.setOnClickListener {
            basicDialog.dismiss()
        }

        basicDialog.id_positive.setOnClickListener {
            val email = basicDialog.id_email_password_reset.text.toString().trim()
            Log.d(CLIENT_TAG, "[SignInActivity]: email to reset password: $email")

            if (email.isEmpty() || email.isBlank()) {
                Toast.makeText(this, "Please enter in a valid email.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            sendResetEmail(email)
            basicDialog.dismiss()
        }
    }

    private fun sendResetEmail(email: String) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Reset password email sent success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignInActivity::class.java))
            } else {
                Toast.makeText(this, "Incorrectly email address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableHideKeyboard() {
        id_main_frame_layout.setOnClickListener {
            ScreenManager.hideKeyBoard(this)
        }
    }

    override fun onResume() {
        super.onResume()
        id_forgot_password.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }
}

