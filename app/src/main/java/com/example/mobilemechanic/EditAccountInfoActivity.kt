package com.example.mobilemechanic

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.registration.fragments.ACCOUNT_DOC_PATH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_account_info.*
import kotlinx.android.synthetic.main.fragment_address_info.*

class EditAccountInfoActivity() : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account_info)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        val currentUser = mAuth?.currentUser

        if(currentUser != null) {
            setUpInfo(currentUser)
        }

        btn_updateAccountInfo.setOnClickListener {
            if(validateInfo()) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpInfo(currentUser: FirebaseUser) {
        val uid = currentUser.uid

        mFireStore?.collection(ACCOUNT_DOC_PATH)?.document(uid)?.get()
            ?.addOnSuccessListener {
                val userInfo = it.toObject(User::class.java)

                id_editEmail.setText(userInfo?.basicInfo?.email.toString())
                id_editEmail.isEnabled = false
                id_editPassword.setText(userInfo?.password.toString())
                id_editFirstName.setText(userInfo?.basicInfo?.firstName.toString())
                id_editLastName.setText(userInfo?.basicInfo?.lastName.toString())
                id_editPhoneNumber.setText(userInfo?.basicInfo?.phoneNumber.toString())
                id_editStreet.setText(userInfo?.address?.street)
                id_editCity.setText(userInfo?.address?.city)
                id_editZipcode.setText(userInfo?.address?.zipCode)

                val state = userInfo?.address?.state.toString()
                setUpSpinner(state)

            }?.addOnFailureListener {

            }
    }

    private fun setUpSpinner(state: String) {
        val spinner: Spinner = findViewById(R.id.id_editStateSpinner)
        spinner.onItemSelectedListener = this
        val states = DataProviderManager.getAllStates()
        val spinnerAdapter = HintSpinnerAdapter(this, android.R.layout.simple_spinner_item, states, "States")
        spinner.adapter = spinnerAdapter

        val index = states.indexOf(state)
        spinner.setSelection(index)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //editModel.state.value = id_editStateSpinner.selectedItem.toString()
    }

    private fun validateInfo(): Boolean {
        val password = id_editPassword.text.toString().trim()
        val firstName = id_editFirstName.text.toString().trim()
        val lastName = id_editLastName.text.toString().trim()
        val phoneNumber = id_editPhoneNumber.text.toString().trim()
        val street = id_editStreet.text.toString().trim()
        val city = id_editCity.text.toString().trim()
        val state = id_editStateSpinner.selectedItem.toString().trim()
        val zip = id_editZipcode.text.toString().trim()

        if (password.length < 6) {
            Toast.makeText(this, "Invalid password!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (firstName.isNullOrEmpty() || firstName.isNullOrBlank()) {
            Toast.makeText(this, "Invalid first name!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (lastName.isNullOrEmpty() || lastName.isNullOrBlank()) {
            Toast.makeText(this, "Invalid last name!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (phoneNumber.length != 10) {
            Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (street.isNullOrEmpty() || street.isNullOrBlank()) {
            Toast.makeText(this, "Invalid address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (city.isNullOrEmpty() || city.isNullOrBlank()) {
            Toast.makeText(this, "Invalid city!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (state.equals("State")) {
            Toast.makeText(this, "Invalid state!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (zip.length != 5) {
            Toast.makeText(this, "Invalid zip!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


}
