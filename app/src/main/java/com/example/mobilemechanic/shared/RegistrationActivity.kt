package com.example.mobilemechanic.shared

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.dto.Address
import com.example.mobilemechanic.model.dto.BasicInfo
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*

const val ACCOUNT_DOC_PATH = "Accounts"
class RegistrationActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        setUpRegistrationActivity()
    }

    private fun setUpRegistrationActivity() {
        setUpStateSpinner()
        setUpToolBar()
        id_register_button.setOnClickListener {
            createUserAccount()
        }

        id_register_signIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        enableHideKeyboard()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_registration_toolbar as android.support.v7.widget.Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Registration Form"
            subtitle = "Create a new account today"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun createUserAccount() {
        val userType = getUserType(id_registration_client.isChecked)
        val email = id_registration_email.text.toString().trim()
        val password = id_registration_password.text.toString().trim()
        val firstName = id_registration_firstName.text.toString().trim()
        val lastName = id_registration_lastName.text.toString().trim()
        val phoneNumber = id_registration_phoneNumber.text.toString().trim()
        val street = id_registration_address.text.toString().trim()
        val city = id_registration_city.text.toString().trim()
        val state = findViewById<Spinner>(R.id.id_registration_state).selectedItem.toString().trim()
        val zip = id_registration_zipcode.text.toString().trim()

        if(validateInformation(email, password, firstName, lastName, phoneNumber, street, city, state, zip)) {
            val address = Address(street, city, state, zip)
            val basicInfo = BasicInfo(firstName, lastName, email, phoneNumber, "")
            val user = User("", "", password, userType, basicInfo, address, 0f)

            // get Firebase FCM token
            FirebaseInstanceId.getInstance().instanceId
                ?.addOnCompleteListener {
                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    }

                    val token = it.result?.token

                    mAuth?.createUserWithEmailAndPassword(email, password)
                        ?.addOnCompleteListener {
                            user.uid = mAuth?.uid.toString()
                            user.fcmToken = token.toString()
                            Toast.makeText(this, user.fcmToken, Toast.LENGTH_SHORT).show()
                            handleAccountCreationSuccess(it, user)
                        }
                }
        }
    }

    private fun handleAccountCreationSuccess(it: Task<AuthResult>, user: User) {
        if(it.isSuccessful) {
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
            saveUser(user)
        } else {
            Toast.makeText(this, "Account created failed!\n${it.exception.toString()}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUser(userInfo: User) {
        mFireStore?.collection(ACCOUNT_DOC_PATH)
            ?.document(userInfo.uid)
            ?.set(userInfo)
            ?.addOnSuccessListener {
                Toast.makeText(this, "Account info added!", Toast.LENGTH_SHORT).show()

                goToUploadPictureActivity(userInfo.userType)
            }
            ?.addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToUploadPictureActivity(userType: UserType) {
        startActivity(Intent(this, ProfilePictureActivity::class.java))
        finish()
    }


    private fun getUserType(isClient: Boolean): UserType {
        return  if (isClient) UserType.CLIENT
                else UserType.MECHANIC
    }

    private fun setUpStateSpinner() {
        val states = DataProviderManager.getAllStates()

        id_registration_state.adapter =
            HintSpinnerAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                states
            )
    }

    private fun validateInformation(email: String,
                                    password: String,
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

    private fun enableHideKeyboard() {
        id_registration_main_frame_layout.setOnClickListener {
            ScreenManager.hideKeyBoard(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        id_register_signIn.paintFlags = id_register_signIn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }
}
