package com.example.mobilemechanic.shared.registration.fragments

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.registration.RegistrationViewModel

class InfoFragment : Fragment() {
    private lateinit var registrationModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity?.let {
            registrationModel = ViewModelProviders.of(it).get(RegistrationViewModel::class.java)
        }

        setUpPager()
    }

    private fun setUpPager() {
        val pager = activity?.findViewById<ViewPager>(R.id.id_registrationPager)
        val profilePictureButton = activity?.findViewById<Button>(R.id.btn_profilePicture)
        val backToCredentials = activity?.findViewById<TextView>(R.id.btn_backToCredentials)

        profilePictureButton?.setOnClickListener {
            if (validateInfo(registrationModel)) {
                pager?.setCurrentItem(2, true)
            }
        }

        backToCredentials?.setOnClickListener {
            pager?.setCurrentItem(0, true)
        }
    }

    private fun validateInfo(registrationModel: RegistrationViewModel) : Boolean {
        val firstNameEditText = activity?.findViewById<EditText>(R.id.id_registrationFirstName)
        val lastNameEditText = activity?.findViewById<EditText>(R.id.id_registrationLastName)
        val phoneNumberEditText = activity?.findViewById<EditText>(R.id.id_registrationPhoneNumber)

        val firstName = firstNameEditText?.text.toString().trim()
        val lastName = lastNameEditText?.text.toString().trim()
        val phoneNumber = phoneNumberEditText?.text.toString().trim()

        if(firstName.isNullOrEmpty() || firstName.isNullOrBlank()) {
            Toast.makeText(activity, "Invalid first otherMemberName!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(lastName.isNullOrEmpty() || lastName.isNullOrBlank()) {
            Toast.makeText(activity, "Invalid last otherMemberName!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(phoneNumber.length != 10) {
            Toast.makeText(activity, "Invalid phone number!", Toast.LENGTH_SHORT).show()
            return false
        }

        registrationModel.firstName.value = firstName
        registrationModel.lastName.value = lastName
        registrationModel.phoneNumber.value = phoneNumber

        return true
    }

    override fun onResume() {
        super.onResume()
        val backToCredentials = activity?.findViewById<TextView>(R.id.btn_backToCredentials)
        backToCredentials?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
}
