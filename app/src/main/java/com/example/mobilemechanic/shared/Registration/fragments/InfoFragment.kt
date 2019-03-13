package com.example.mobilemechanic.shared.Registration.fragments

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.Registration.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_info.*

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

        btn_profilePicture.setOnClickListener {
            if (validateInfo(registrationModel)) {
                pager?.setCurrentItem(2, true)
            }
        }

        btn_backToCredentials.setOnClickListener {
            pager?.setCurrentItem(0, true)
        }
    }

    private fun validateInfo(registrationModel: RegistrationViewModel) : Boolean {
        val firstName = id_registrationFirstName.text.toString().trim()
        val lastName = id_registrationLastName.text.toString().trim()
        val phoneNumber = id_registrationPhoneNumber.text.toString().trim()

        if(firstName.isNullOrEmpty() || firstName.isNullOrBlank()) {
            Toast.makeText(activity, "Invalid first name!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(lastName.isNullOrEmpty() || lastName.isNullOrBlank()) {
            Toast.makeText(activity, "Invalid last name!", Toast.LENGTH_SHORT).show()
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
        btn_backToCredentials.paintFlags = btn_backToCredentials.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}
