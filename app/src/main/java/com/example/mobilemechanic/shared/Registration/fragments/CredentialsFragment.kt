package com.example.mobilemechanic.shared.Registration.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.Registration.RegistrationViewModel
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.shared.SignInActivity
import kotlinx.android.synthetic.main.fragment_credentials.*

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

        btn_personalInfo.setOnClickListener {

            if (validateCredentials(registrationModel)) {
                pager?.setCurrentItem(1, true)
            }
        }
        btn_backToSignIn.setOnClickListener {
            startActivity(Intent(activity, SignInActivity::class.java))
        }
    }

    private fun getUserType(isClient: Boolean): UserType {
        return  if (isClient) UserType.CLIENT
        else UserType.MECHANIC
    }

    private fun validateCredentials(registrationModel: RegistrationViewModel) : Boolean {
        val userType = getUserType(id_clientType.isChecked)
        val email = id_registrationEmail.text.toString().trim()
        val password = id_registration_password.text.toString().trim()

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

        return true
    }

}
