package com.example.mobilemechanic.shared.Registration.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.dto.Address
import com.example.mobilemechanic.model.dto.BasicInfo
import com.example.mobilemechanic.shared.HintSpinnerAdapter
import com.example.mobilemechanic.shared.Registration.RegistrationViewModel
import com.example.mobilemechanic.shared.USER_TAG
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_address_info.*

const val ACCOUNT_DOC_PATH = "Accounts"

class AddressInfoFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var registrationModel: RegistrationViewModel

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFireStore: FirebaseFirestore
    private lateinit var mStorage: FirebaseStorage
    private lateinit var profileImageStorageRef: StorageReference
    private var selectedImageUri: Uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
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
            if (validateAddress(registrationModel)) {
                createUserAccount(registrationModel)
            } else {
                Toast.makeText(activity, "Invalid address information", Toast.LENGTH_LONG).show()
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
        val spinnerAdapter = HintSpinnerAdapter(context!!, android.R.layout.simple_spinner_item, states, "State")
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        registrationModel.state.value = id_registrationStateSpinner.selectedItem.toString()
    }

    private fun validateAddress(registrationModel: RegistrationViewModel): Boolean {
        val street = id_registrationStreet.text.toString().trim()
        val city = id_registrationCity.text.toString().trim()
        val state = id_registrationStateSpinner.selectedItem.toString().trim()
        val zip = id_registrationZip.text.toString().trim()

        if (street.isNullOrEmpty() || street.isNullOrBlank()) {
            Toast.makeText(activity, "Invalid address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (city.isNullOrEmpty() || city.isNullOrBlank()) {
            Toast.makeText(activity, "Invalid city!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (state.equals("State")) {
            Toast.makeText(activity, "Invalid state!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (zip.length != 5) {
            Toast.makeText(activity, "Invalid zip!", Toast.LENGTH_SHORT).show()
            return false
        }

        registrationModel.streetAddress.value = street
        registrationModel.city.value = city
        registrationModel.state.value = state
        registrationModel.zipcode.value = zip

        return true
    }

    private fun createUserAccount(registrationModel: RegistrationViewModel) {
        val userType = registrationModel.userType.value
        val email = registrationModel.emailAddress.value
        val password = registrationModel.password.value
        val firstName = registrationModel.firstName.value
        val lastName = registrationModel.lastName.value
        val phoneNumber = registrationModel.phoneNumber.value
        val photoUrl = registrationModel.photoUrl.value
        val street = registrationModel.streetAddress.value
        val city = registrationModel.city.value
        val state = registrationModel.state.value
        val zip = registrationModel.zipcode.value

        if ((!(street.isNullOrEmpty() || street.isNullOrBlank())) &&
            (!(city.isNullOrEmpty() || city.isNullOrBlank())) &&
            (!(state.isNullOrEmpty() || state.isNullOrBlank())) &&
            (!(zip.isNullOrEmpty() || zip.isNullOrBlank())) &&
            (!(firstName.isNullOrEmpty() || firstName.isNullOrBlank())) &&
            (!(lastName.isNullOrEmpty() || lastName.isNullOrBlank())) &&
            (!(phoneNumber.isNullOrEmpty() || phoneNumber.isNullOrBlank())) &&
            (!(email.isNullOrEmpty() || email.isNullOrBlank())) &&
            (!(password.isNullOrEmpty() || password.isNullOrBlank()))
        ) {

            val address = Address(street, city, state, zip)
            val basicInfo = BasicInfo(firstName, lastName, email, phoneNumber, "")
            val user = User("", "", password, userType!!, basicInfo, address, 0f)

            if (photoUrl != null) {
                selectedImageUri = photoUrl
            }

            createUserAccountOnFirebase(user)
        }
    }

    private fun createUserAccountOnFirebase(user: User) {
        mAuth?.createUserWithEmailAndPassword(user.basicInfo.email, user.password)
            ?.addOnCompleteListener {
                handleAccountCreationSuccess(it, user)
            }
    }

    private fun handleAccountCreationSuccess(it: Task<AuthResult>, user: User) {
        if (it.isSuccessful) {
            Toast.makeText(activity, "Registration completed.", Toast.LENGTH_SHORT).show()
            val currentUserUid = mAuth.currentUser?.uid
            if (!currentUserUid.isNullOrEmpty()) {
                user.uid = currentUserUid
            }

            saveUserToFirestore(user)
        } else {
            Toast.makeText(activity, "Registration failed. Please try another email.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserToFirestore(user: User) {
        mFireStore.collection(ACCOUNT_DOC_PATH).document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(USER_TAG, "[AddressInfoFragment] Account information saved to firestore")
                Toast.makeText(activity, "Account saved.", Toast.LENGTH_SHORT).show()
                if (!Uri.EMPTY.equals(selectedImageUri)) {
                    saveImageToFireStorage(user)
                } else {
                    redirectUserToWelcomePage()
                }
            }
            .addOnFailureListener {
                Log.d(USER_TAG, "[AddressInfoFragment] ${it.message}")
            }
    }

    private fun redirectUserToWelcomePage() {
        val userType = registrationModel.userType.value
        if (userType == UserType.MECHANIC) {
            Log.d(USER_TAG, "[AddressInfoFragment] Redirect to mechanic welcome page")
            startActivity(Intent(activity, MechanicWelcomeActivity::class.java))
        } else {
            Log.d(USER_TAG, "[AddressInfoFragment] Redirect to client welcome page")
            startActivity(Intent(activity, ClientWelcomeActivity::class.java))
        }

        activity!!.finish()
    }

    private fun saveImageToFireStorage(user: User) {
        Log.d(CLIENT_TAG, "[ProfilePictureActivity] uploadProfileImage() to firestorage for uid: $user.uid")
        profileImageStorageRef = mStorage?.reference?.child("Accounts/${user.uid}/profile_image")

        profileImageStorageRef?.putFile(selectedImageUri as Uri)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    handleUploadImageSuccess(it, user)
                }
            }
    }

    private fun handleUploadImageSuccess(it: Task<UploadTask.TaskSnapshot>, user: User) {
        if (it.isSuccessful) {
            profileImageStorageRef.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                mFireStore.collection("Accounts").document(user.uid).update("basicInfo.photoUrl", "$downloadUrl")
                    .addOnSuccessListener {
                        Log.d(USER_TAG, "[AddressInfoFragment] user photoUrl saved to firestore successfully")

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("${user.basicInfo.firstName} ${user.basicInfo.lastName}")
                            .setPhotoUri(Uri.parse(downloadUrl))
                            .build()

                        mAuth.currentUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.d(USER_TAG, "[AddressInfoFragment] user profile udpated")
                                    redirectUserToWelcomePage()
                                }
                            }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        btn_backToUploadProfilePicture.paintFlags = btn_backToUploadProfilePicture.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}
