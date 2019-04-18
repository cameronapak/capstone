package com.example.mobilemechanic.shared.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.mobilemechanic.shared.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth

object AuthenticationManager {
    private var mAuth = FirebaseAuth.getInstance()
    fun signInGuard(context: Context) {
        val user = mAuth?.currentUser
        if (user == null) {
            context.startActivity(Intent(context, SignInActivity::class.java))
            (context as Activity).finish()
        }
    }
}