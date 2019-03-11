package com.example.mobilemechanic.shared.Registration.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast

import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.RegistrationViewModel
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import kotlinx.android.synthetic.main.fragment_address_info.*
import kotlinx.android.synthetic.main.fragment_info.*

class AddressInfoFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var registrationModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_address_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity?.let {
            registrationModel = ViewModelProviders.of(it).get(RegistrationViewModel::class.java)
        }

        setUpSpinner(view)
        setUpPager()
    }

    private fun setUpPager() {
        val pager = activity?.findViewById<ViewPager>(R.id.id_registrationPager)

        btn_registerAccount.setOnClickListener {
            if(validateAddress(registrationModel)) {
                Toast.makeText(activity, "COMPLETE!", Toast.LENGTH_SHORT).show()

                var intent = Intent()

                if(registrationModel.userType.value == UserType.CLIENT) {
                    intent = Intent(activity, ClientWelcomeActivity::class.java)
                } else if (registrationModel.userType.value == UserType.MECHANIC) {
                    intent = Intent(activity, MechanicWelcomeActivity::class.java)
                }

                startActivity(intent)
                activity?.finish()
            }
        }

        btn_backToUploadProfilePicture.setOnClickListener {
            pager?.setCurrentItem(2, true)
        }
    }

    private fun setUpSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.id_registrationStateSpinner)
        spinner.onItemSelectedListener = this
        val states = DataProviderManager.getAllStates()
        val spinnerAdapter = HintSpinnerAdapter(context!!, android.R.layout.simple_spinner_item, states)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        registrationModel.state.value = id_registrationStateSpinner.selectedItem.toString()
    }

    private fun validateAddress(registrationModel: RegistrationViewModel) : Boolean {
        val street = id_registrationStreet.text.toString().trim()
        val city = id_registrationCity.text.toString().trim()
        val state = id_registrationStateSpinner.selectedItem.toString().trim()
        val zip = id_registrationZip.text.toString().trim()

        if(street.isNullOrEmpty() || street.isNullOrBlank()) {
            Toast.makeText(activity, "Invalid address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(city.isNullOrEmpty() || city.isNullOrBlank()) {
            Toast.makeText(activity, "Invalid city!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(state.equals("State")) {
            Toast.makeText(activity, "Invalid state!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(zip.length != 5) {
            Toast.makeText(activity, "Invalid zip!", Toast.LENGTH_SHORT).show()
            return false
        }

        registrationModel.streetAddress.value = street
        registrationModel.city.value = city
        registrationModel.state.value = state
        registrationModel.zipcode.value = zip

        return true
    }
}
