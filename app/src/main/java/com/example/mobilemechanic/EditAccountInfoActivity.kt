package com.example.mobilemechanic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.dto.Address
import com.example.mobilemechanic.model.dto.BasicInfo
import com.example.mobilemechanic.model.dto.LatLngHolder
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.registration.fragments.ACCOUNT_DOC_PATH
import com.example.mobilemechanic.shared.signin.USER_TAG
import com.example.mobilemechanic.shared.utility.AddressManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_account_info.*

class EditAccountInfoActivity() : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private var userInfo: User?= null
    private var oldPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account_info)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        setUpEditAccountInfoActivity()
    }

    private fun setUpEditAccountInfoActivity() {
        setUpToolBar()
        setUpForm()
    }

    private fun setUpForm() {
        val currentUser = mAuth?.currentUser
        if(currentUser != null) {
            setUpCurrentInfo(currentUser)
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_edit_settings_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Settings"
            subtitle = "Update your information"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpCurrentInfo(currentUser: FirebaseUser) {
        val uid = currentUser.uid
        mFireStore?.collection(ACCOUNT_DOC_PATH)?.document(uid)?.get()
            ?.addOnSuccessListener {
                userInfo = it.toObject(User::class.java)

                id_editEmail.setText(userInfo?.basicInfo?.email.toString())
                id_editEmail.isEnabled = false
                id_editPassword.setText(userInfo?.password.toString())
                oldPassword = userInfo?.password.toString()
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

    private fun saveUserInfo() {
        if(validateInfo()) {
            getNewInformationFromForm(mAuth?.currentUser!!)
        } else {
            Log.d(USER_TAG, "Invalid information")
        }
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

    private fun getNewInformationFromForm(currentUser: FirebaseUser) {
        val email = id_editEmail.text.toString().trim()
        val password = id_editPassword.text.toString().trim()
        val firstName = id_editFirstName.text.toString().trim()
        val lastName = id_editLastName.text.toString().trim()
        val phoneNumber = id_editPhoneNumber.text.toString().trim()
        val street = id_editStreet.text.toString().trim()
        val city = id_editCity.text.toString().trim()
        val state = id_editStateSpinner.selectedItem.toString().trim()
        val zip = id_editZipcode.text.toString().trim()
        val photoUrl = currentUser?.photoUrl.toString()

        val address = Address(street, city, state, zip, LatLngHolder())

        val userLatLng = AddressManager.convertAddressToLatLng(this, address)
        val latlngHolder = LatLngHolder(userLatLng.latitude, userLatLng.longitude)
        address._geoloc = latlngHolder

        val basicInfo = BasicInfo(firstName, lastName, email, phoneNumber, photoUrl)

        val user = User(userInfo?.uid.toString(), userInfo?.fcmToken.toString(), password,
            userInfo!!.userType, basicInfo, address, userInfo!!.rating)

        updateInfo(user)
    }

    private fun updateInfo(user: User) {
        val uid = user.uid

        mFireStore?.collection("Accounts")
            ?.document(uid)
            ?.set(user)
            ?.addOnSuccessListener {
            Toast.makeText(this, "Account information saved!", Toast.LENGTH_SHORT).show()
                updateUserProfile(user)
        }

        if(!oldPassword.equals(user.password)) {
            val currentUser = mAuth?.currentUser
            currentUser?.updatePassword(user.password)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "Password updated failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun updateUserProfile(userInfo: User) {
        val user = mAuth?.currentUser
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("${userInfo.basicInfo.firstName} ${userInfo.basicInfo.lastName}")
                .setPhotoUri(Uri.parse("${userInfo.basicInfo.photoUrl}"))
                .build()
            user.updateProfile(profileUpdates).addOnSuccessListener {
                goToHomeActivity()
//                onBackPressed()
            }
        }
    }


    private fun goToHomeActivity() {
        if(userInfo!!.userType.equals(UserType.MECHANIC)) {
            startActivity(Intent(this, MechanicWelcomeActivity::class.java))
        } else if (userInfo!!.userType.equals(UserType.CLIENT)){
            startActivity(Intent(this, ClientWelcomeActivity::class.java))
        }

        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        saveUserInfo()
        Log.d(CLIENT_TAG, "Save user information and go back to previous activity")
        return true
    }
}
