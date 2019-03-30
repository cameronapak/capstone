package com.example.mobilemechanic.shared.registration.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.shared.registration.RegistrationViewModel
import com.example.mobilemechanic.shared.signin.SignInActivity
import com.example.mobilemechanic.shared.signin.USER_TAG

class CredentialsFragment : Fragment() {
    private lateinit var registrationModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_credentials, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity?.let {
            registrationModel = ViewModelProviders.of(it).get(RegistrationViewModel::class.java)
        }

        setUpPager()
    }

    private fun setUpPager() {
        val pager = activity?.findViewById<ViewPager>(R.id.id_registrationPager)
        val personalInfoButton = activity?.findViewById<Button>(R.id.btn_personalInfo)
        val backToSignIn = activity?.findViewById<TextView>(R.id.btn_backToSignIn)

        personalInfoButton?.setOnClickListener {
            if (validateCredentials(registrationModel)) {
                pager?.setCurrentItem(1, true)
            }
        }

        backToSignIn?.setOnClickListener {
            startActivity(Intent(activity, SignInActivity::class.java))
            activity?.finish()
        }
    }

    private fun getUserType(isClient: Boolean): UserType {
        return  if (isClient) UserType.CLIENT
        else UserType.MECHANIC
    }

    private fun validateCredentials(registrationModel: RegistrationViewModel) : Boolean {
        val clientType = activity?.findViewById<RadioButton>(R.id.id_clientType)
        val registrationEmail = activity?.findViewById<EditText>(R.id.id_registrationEmail)
        val registrationPassword = activity?.findViewById<TextInputEditText>(R.id.id_registration_password)

        val userType = getUserType(clientType?.isChecked!!)
        val email = registrationEmail?.text.toString().trim()
        val password = registrationPassword?.text.toString().trim()

        if(email.isNullOrEmpty() || email.isNullOrBlank() || !email.contains('@')) {
            Toast.makeText(activity, "Invalid email address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(password.length < 6) {
            Toast.makeText(activity, "Password should have a minimum of 6 characters.", Toast.LENGTH_SHORT).show()
            return false
        }

        registrationModel.userType.value = userType
        registrationModel.emailAddress.value = email
        registrationModel.password.value = password

        Log.d(USER_TAG, "[CredentialsFragment] $registrationModel")
        return true
    }

    override fun onResume() {
        super.onResume()
        val backToSignIn = activity?.findViewById<TextView>(R.id.btn_backToSignIn)
        backToSignIn?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
}
