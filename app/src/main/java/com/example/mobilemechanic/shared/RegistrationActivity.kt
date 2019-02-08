package com.example.mobilemechanic.shared

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*

const val ACCOUNT_DOC_PATH = "account/accountDoc"
const val USER_INFO = "user_info"

class RegistrationActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private var fileUriPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        setUpStateSpinner()

        id_register_button.setOnClickListener {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        val userType = getUserType(id_registration_client.isChecked)
        val email = id_registration_email.text.toString().trim()
        val password = id_registration_password.text.toString().trim()
        val passwordConfirmation = id_registration_passwordConfirmation.text.toString().trim()
        val firstName = id_registration_firstName.text.toString().trim()
        val lastName = id_registration_lastName.text.toString().trim()
        val phoneNumber = id_registation_phoneNumber.text.toString().trim()
        val address = id_registration_address.text.toString().trim()
        val city = id_registration_city.text.toString().trim()
        val state = findViewById<Spinner>(R.id.id_registration_state).selectedItem.toString().trim()
        val zip = id_registration_zipcode.text.toString().trim()

        if(validateInformation(email, password, passwordConfirmation,
                firstName, lastName, phoneNumber, address, city, state, zip)) {

            val userInfo = User(userType, email, password, passwordConfirmation, firstName,
                lastName, phoneNumber, address, city, state, zip)

            mAuth?.createUserWithEmailAndPassword(userInfo.email, userInfo.password)
                ?.addOnCompleteListener {
                    handleAccountCreationSuccess(it, userInfo)
            }
        }
    }

    private fun handleAccountCreationSuccess(it: Task<AuthResult>, userInfo: User) {
        if(it.isSuccessful) {
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
            saveUserInfo(userInfo)
        } else {
            Toast.makeText(this, "Account created failed!\n${it.exception.toString()}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserInfo(userInfo: User) {
        mFireStore?.collection("$ACCOUNT_DOC_PATH/${userInfo.email}")
            ?.document(USER_INFO)
            ?.set(userInfo)
            ?.addOnSuccessListener {
                Toast.makeText(this, "Account info added!", Toast.LENGTH_SHORT).show()
                goToMainActivity(userInfo.userType)
            }
            ?.addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToMainActivity(userType: String) {
        var intent = Intent()

        if(userType.equals("client")) {
            intent = Intent(this, ClientWelcomeActivity::class.java)
        }

        //for mechanic activity
        /*else if(userType.equals("mechanic")) {
            intent = Intent(this, MechanicWelcomeActivity::class.java)
        }*/

        startActivity(intent)
        finish()
    }


    private fun getUserType(isClient: Boolean): String {
        if(isClient) return "client"
        else return "mechanic"
    }

    private fun setUpStateSpinner() {
        val states = DataProviderManager.getAllStates()

        id_registration_state.adapter =
            StatesSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, states)
    }

    private fun validateInformation(email: String,
                                    password: String,
                                    passwordConfirmation: String,
                                    firstName: String,
                                    lastName: String,
                                    phoneNumber: String,
                                    address: String,
                                    city: String,
                                    state: String,
                                    zip: String) : Boolean {

        if(email.isNullOrEmpty() || email.isNullOrBlank() || !email.contains('@')) {
            Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(password.length < 6) {
            Toast.makeText(this, "Password should have a minimum of 6 characters.", Toast.LENGTH_SHORT).show()
            return false
        }

        if(!passwordConfirmation.equals(password)) {
            Toast.makeText(this, "Password and Password Confirmation don't match.", Toast.LENGTH_SHORT).show()
            return false
        }

        if(firstName.isNullOrEmpty() || firstName.isNullOrBlank()) {
            Toast.makeText(this, "Invalid first name!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(lastName.isNullOrEmpty() || lastName.isNullOrBlank()) {
            Toast.makeText(this, "Invalid last name!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(phoneNumber.length != 10) {
            Toast.makeText(this, "Invalid phone number!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(address.isNullOrEmpty() || address.isNullOrBlank()) {
            Toast.makeText(this, "Invalid address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(city.isNullOrEmpty() || city.isNullOrBlank()) {
            Toast.makeText(this, "Invalid city!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(state.equals("State")) {
            Toast.makeText(this, "Invalid state!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(zip.length != 5) {
            Toast.makeText(this, "Invalid zip!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}
