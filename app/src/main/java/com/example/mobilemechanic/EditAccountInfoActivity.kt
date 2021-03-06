package com.example.mobilemechanic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
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
import com.example.mobilemechanic.shared.Toasty
import com.example.mobilemechanic.shared.ToastyType
import com.example.mobilemechanic.shared.registration.fragments.ACCOUNT_DOC_PATH
import com.example.mobilemechanic.shared.signin.USER_TAG
import com.example.mobilemechanic.shared.utility.AddressManager
import com.example.mobilemechanic.shared.utility.AuthenticationManager
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_edit_account_info.*

class EditAccountInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFireStore: FirebaseFirestore
    private lateinit var mStorage: FirebaseStorage
    private var userInfo: User? = null
    private var oldPassword: String = ""
    private var selectedImageUri: Uri? = null
    private lateinit var profileImageStorageRef: StorageReference

    private lateinit var accountRef: CollectionReference
    private lateinit var chatRef: CollectionReference
    private lateinit var requestRef: CollectionReference
    private lateinit var reviewRef: CollectionReference
    private lateinit var serviceRef: CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account_info)

        initFireStore()
        setUpEditAccountInfoActivity()
    }

    private fun initFireStore() {
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()

        accountRef = mFireStore.collection("Accounts")
        chatRef = mFireStore.collection("ChatRooms")
        requestRef = mFireStore.collection("Requests")
        reviewRef = mFireStore.collection("Reviews")
        serviceRef = mFireStore.collection("Services")
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

        id_btn_update_profile.setOnClickListener {
            id_btn_update_profile.isEnabled = false
            id_btn_update_profile.text = "Saving Changes..."
            saveUserInfo()
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

    private fun openGallery() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setFixAspectRatio(true)
            .setAspectRatio(1, 1)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                val resultUri = result.uri
                updateProfilePicture(resultUri)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toasty.makeText(this, "${error.message}", ToastyType.FAIL)
            }
        }
    }

    private fun updateProfilePicture(imageUri: Uri) {
        Log.d(CLIENT_TAG, "[EditAccountInfoActivity] Update Profile Image!")
        if (imageUri != null) {
            val profilePictureCircleImage = id_settings_profile_image
            if (profilePictureCircleImage != null) {
                profilePictureCircleImage.setImageDrawable(null)
                selectedImageUri = imageUri
                Log.d(CLIENT_TAG, "[EditAccountInfoActivity] convert Uri to bitmap for compression")
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                profilePictureCircleImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun setUpProfilePicture() {
        val userProfileUri = mAuth?.currentUser?.photoUrl
        Log.d(CLIENT_TAG, "[EditAccountInfoActivity] photoUrl ${userProfileUri}")

        if (userProfileUri != null) {
            Picasso.get().load(userProfileUri).into(id_settings_profile_image)
        } else {
            Picasso.get().load(R.drawable.ic_circle_profile).into(id_settings_profile_image)
        }

        id_settings_profile_image.setOnClickListener {
            openGallery()
            true
        }
    }

    private fun setUpCurrentInfo(currentUser: FirebaseUser) {
        val profileImage = findViewById<CircleImageView>(R.id.id_profile_image)
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
                setUpProfilePicture()
            }?.addOnFailureListener { }
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

    private fun uploadProfilePhoto() {
        saveImageToFireStorage(userInfo!!)
    }

    private fun saveUserInfo() {
        if(validateInfo()) {
            if (selectedImageUri != null)
                uploadProfilePhoto()
            else
                getNewInformationFromForm(mAuth?.currentUser!!)
        } else {
            Log.d(USER_TAG, "Invalid information")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

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
            Toasty.makeText(this, "Warning", ToastyType.WARNING)
            return false
        }

        if (firstName.isNullOrEmpty() || firstName.isNullOrBlank()) {
            Toasty.makeText(this, "Warning", ToastyType.WARNING)
            return false
        }

        if (lastName.isNullOrEmpty() || lastName.isNullOrBlank()) {
            Toasty.makeText(this, "Warning", ToastyType.WARNING)
            return false
        }

        if (phoneNumber.length != 10) {
            Toasty.makeText(this, "Warning", ToastyType.WARNING)
            return false
        }

        if (street.isNullOrEmpty() || street.isNullOrBlank()) {
            Toasty.makeText(this, "Warning", ToastyType.WARNING)
            return false
        }

        if (city.isNullOrEmpty() || city.isNullOrBlank()) {
            Toasty.makeText(this, "Warning", ToastyType.WARNING)
            return false
        }

        if (state.equals("State")) {
            Toasty.makeText(this, "Warning", ToastyType.WARNING)
            return false
        }

        if (zip.length != 5) {
            Toasty.makeText(this, "Warning", ToastyType.WARNING)
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
        var photoUrl = if (selectedImageUri != null) userInfo!!.basicInfo.photoUrl else currentUser?.photoUrl.toString()
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
                updateOtherCollectionInfo(user)
                Toasty.makeText(this, "Account updated", ToastyType.SUCCESS)
                updateUserProfile(user)
        }

        if(!oldPassword.equals(user.password)) {
            val currentUser = mAuth?.currentUser
            currentUser?.updatePassword(user.password)
                ?.addOnSuccessListener {
                    Toasty.makeText(this, "Password updated", ToastyType.SUCCESS)
                }
                ?.addOnFailureListener {
                    Toasty.makeText(this, "Password change failed. Try again.", ToastyType.FAIL)
                }
        }
    }

    private fun updateOtherCollectionInfo(user: User) {

        if(user.userType.equals(UserType.MECHANIC)) {

            //update info in chatrooms collection
            chatRef.whereEqualTo("mechanicMember.uid", user.uid).get()
                .addOnCompleteListener {
                    val batch = mFireStore.batch()

                    if(it.isSuccessful) {
                        for(item in it.result!!) {
                            val path = chatRef.document(item.id)
                            batch.update(path,"mechanicMember.firstName", user.basicInfo.firstName)
                            batch.update(path,"mechanicMember.lastName", user.basicInfo.lastName)
                            batch.update(path,"mechanicMember.photoUrl", user.basicInfo.photoUrl)
                        }
                    }
                    batch.commit()
            }

            //update info in messages in chatrooms collection
            chatRef.whereEqualTo("mechanicMember.uid", user.uid).get()
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        for(item in it.result!!) {
                            chatRef.document(item.id).collection("Messages").whereEqualTo("chatUserInfo.uid", user.uid).get()
                                .addOnCompleteListener {
                                    val batch = mFireStore.batch()
                                    if(it.isSuccessful) {
                                        for(message in it.result!!) {
                                            val path = chatRef.document(item.id).collection("Messages").document(message.id)
                                            batch.update(path,"chatUserInfo.firstName", user.basicInfo.firstName)
                                            batch.update(path,"chatUserInfo.lastName", user.basicInfo.lastName)
                                            batch.update(path,"chatUserInfo.photoUrl", user.basicInfo.photoUrl)
                                        }
                                    }
                                    batch.commit()
                                }
                        }
                    }
                }

            //update info in requests collection
            requestRef.whereEqualTo("mechanicInfo.uid", user.uid).get()
                .addOnCompleteListener {
                    val batch = mFireStore.batch()

                    if(it.isSuccessful) {
                        for(item in it.result!!) {
                            val path = requestRef.document(item.id)
                            batch.update(path,"mechanicInfo.address._geoloc.lat", user.address._geoloc.lat)
                            batch.update(path,"mechanicInfo.address._geoloc.lng", user.address._geoloc.lng)
                            batch.update(path,"mechanicInfo.address.city", user.address.city)
                            batch.update(path,"mechanicInfo.address.state", user.address.state)
                            batch.update(path,"mechanicInfo.address.street", user.address.street)
                            batch.update(path,"mechanicInfo.address.zipCode", user.address.zipCode)

                            batch.update(path,"mechanicInfo.basicInfo.firstName", user.basicInfo.firstName)
                            batch.update(path,"mechanicInfo.basicInfo.lastName", user.basicInfo.lastName)
                            batch.update(path,"mechanicInfo.basicInfo.phoneNumber", user.basicInfo.phoneNumber)
                            batch.update(path,"mechanicInfo.basicInfo.photoUrl", user.basicInfo.photoUrl)
                        }
                    }
                    batch.commit()
                }

            //update info in reviews collection
            reviewRef.whereEqualTo("mechanicInfo.uid", user.uid).get()
                .addOnCompleteListener {
                    val batch = mFireStore.batch()

                    if(it.isSuccessful) {
                        for(item in it.result!!) {
                            val path = reviewRef.document(item.id)
                            batch.update(path,"mechanicInfo.address._geoloc.lat", user.address._geoloc.lat)
                            batch.update(path,"mechanicInfo.address._geoloc.lng", user.address._geoloc.lng)
                            batch.update(path,"mechanicInfo.address.city", user.address.city)
                            batch.update(path,"mechanicInfo.address.state", user.address.state)
                            batch.update(path,"mechanicInfo.address.street", user.address.street)
                            batch.update(path,"mechanicInfo.address.zipCode", user.address.zipCode)

                            batch.update(path,"mechanicInfo.basicInfo.firstName", user.basicInfo.firstName)
                            batch.update(path,"mechanicInfo.basicInfo.lastName", user.basicInfo.lastName)
                            batch.update(path,"mechanicInfo.basicInfo.phoneNumber", user.basicInfo.phoneNumber)
                            batch.update(path,"mechanicInfo.basicInfo.photoUrl", user.basicInfo.photoUrl)
                        }
                    }
                    batch.commit()
                }

            //update info in reviews collection
            serviceRef.whereEqualTo("mechanicInfo.uid", user.uid).get()
                .addOnCompleteListener {
                    val batch = mFireStore.batch()

                    if(it.isSuccessful) {
                        for(item in it.result!!) {
                            val path = serviceRef.document(item.id)
                            batch.update(path,"_geoloc.lat", user.address._geoloc.lat)
                            batch.update(path,"_geoloc.lng", user.address._geoloc.lng)

                            batch.update(path,"mechanicInfo.address._geoloc.lat", user.address._geoloc.lat)
                            batch.update(path,"mechanicInfo.address._geoloc.lng", user.address._geoloc.lng)
                            batch.update(path,"mechanicInfo.address.city", user.address.city)
                            batch.update(path,"mechanicInfo.address.state", user.address.state)
                            batch.update(path,"mechanicInfo.address.street", user.address.street)
                            batch.update(path,"mechanicInfo.address.zipCode", user.address.zipCode)

                            batch.update(path,"mechanicInfo.basicInfo.firstName", user.basicInfo.firstName)
                            batch.update(path,"mechanicInfo.basicInfo.lastName", user.basicInfo.lastName)
                            batch.update(path,"mechanicInfo.basicInfo.phoneNumber", user.basicInfo.phoneNumber)
                            batch.update(path,"mechanicInfo.basicInfo.photoUrl", user.basicInfo.photoUrl)
                        }
                    }
                    batch.commit()
                }

        } else if(user.userType.equals(UserType.CLIENT)){

            //update info in chatrooms collection
            chatRef.whereEqualTo("clientMember.uid", user.uid).get()
                .addOnCompleteListener {
                    val batch = mFireStore.batch()

                    if(it.isSuccessful) {
                        for(item in it.result!!) {
                            val path = chatRef.document(item.id)
                            batch.update(path,"clientMember.firstName", user.basicInfo.firstName)
                            batch.update(path,"clientMember.lastName", user.basicInfo.lastName)
                            batch.update(path,"clientMember.photoUrl", user.basicInfo.photoUrl)
                        }
                    }
                    batch.commit()
                }

            //update info in messages in chatrooms collection
            chatRef.whereEqualTo("clientMember.uid", user.uid).get()
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        for(item in it.result!!) {
                            chatRef.document(item.id).collection("Messages").whereEqualTo("chatUserInfo.uid", user.uid).get()
                                .addOnCompleteListener {
                                    val batch = mFireStore.batch()
                                    if(it.isSuccessful) {
                                        for(message in it.result!!) {
                                            val path = chatRef.document(item.id).collection("Messages").document(message.id)
                                            batch.update(path,"chatUserInfo.firstName", user.basicInfo.firstName)
                                            batch.update(path,"chatUserInfo.lastName", user.basicInfo.lastName)
                                            batch.update(path,"chatUserInfo.photoUrl", user.basicInfo.photoUrl)
                                        }
                                    }
                                    batch.commit()
                                }
                        }
                    }
                }

            //update info in requests collection
            requestRef.whereEqualTo("clientInfo.uid", user.uid).get()
                .addOnCompleteListener {
                    val batch = mFireStore.batch()

                    if(it.isSuccessful) {
                        for(item in it.result!!) {
                            val path = requestRef.document(item.id)
                            batch.update(path,"clientInfo.address._geoloc.lat", user.address._geoloc.lat)
                            batch.update(path,"clientInfo.address._geoloc.lng", user.address._geoloc.lng)
                            batch.update(path,"clientInfo.address.city", user.address.city)
                            batch.update(path,"clientInfo.address.state", user.address.state)
                            batch.update(path,"clientInfo.address.street", user.address.street)
                            batch.update(path,"clientInfo.address.zipCode", user.address.zipCode)

                            batch.update(path,"clientInfo.basicInfo.firstName", user.basicInfo.firstName)
                            batch.update(path,"clientInfo.basicInfo.lastName", user.basicInfo.lastName)
                            batch.update(path,"clientInfo.basicInfo.phoneNumber", user.basicInfo.phoneNumber)
                            batch.update(path,"clientInfo.basicInfo.photoUrl", user.basicInfo.photoUrl)
                        }
                    }
                    batch.commit()
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
            }
        }
    }

    private fun saveImageToFireStorage(user: User) {
        Log.d(CLIENT_TAG, "[ProfilePictureActivity] uploadProfileImage() to firestorage for uid: $user.uid")
        profileImageStorageRef = mStorage?.reference?.child("Accounts/${user.uid}/profile_image")

        if(selectedImageUri != null) {
            profileImageStorageRef?.putFile(selectedImageUri as Uri)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        handleUploadImageSuccess(it, user)
                    }
                }
        }
    }

    private fun handleUploadImageSuccess(it: Task<UploadTask.TaskSnapshot>, user: User) {
        if (it.isSuccessful) {
            profileImageStorageRef.downloadUrl
                .addOnSuccessListener {
                    val downloadUrl = it.toString()
                    user.basicInfo.photoUrl = downloadUrl
                    userInfo!!.basicInfo.photoUrl = downloadUrl
                    mFireStore?.collection("Accounts")!!.document(user.uid).update("basicInfo.photoUrl", "$downloadUrl")
                        .addOnSuccessListener {
                            Log.d(USER_TAG, "[AddressInfoFragment] user photoUrl saved to firestore successfully")
                            getNewInformationFromForm(mAuth?.currentUser!!)
                            true
                        }
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

    private fun enableHideKeyboard() {
        id_edit_account_layout.setOnClickListener {
            ScreenManager.hideKeyBoard(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        goToHomeActivity()
        return true
    }

    override fun onResume() {
        AuthenticationManager.signInGuard(this)
        enableHideKeyboard()
        super.onResume()
    }
}
