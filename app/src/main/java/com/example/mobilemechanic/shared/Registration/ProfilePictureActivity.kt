package com.example.mobilemechanic.shared.Registration

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.ClientWelcomeActivity
import com.example.mobilemechanic.mechanic.MechanicWelcomeActivity
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_profile_picture.*

class ProfilePictureActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_profile_picture)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        val currentUser = mAuth?.currentUser

        img_profilePicture.setOnClickListener {
            openGallery()
        }

        btn_uploadProfilePicture.setOnClickListener {
            if (currentUser != null) {
                //Toast.makeText(this, "$selectedImageUri", Toast.LENGTH_SHORT).show()
                if (isImageUriExist()) {
                    uploadProfileImage(currentUser.uid)
                }
            }
        }

        btn_skip.setOnClickListener {
            if (currentUser != null) {
                goToMainActivity(currentUser.uid)
            }
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
                showSelectedProfileImage(resultUri)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSelectedProfileImage(imageUri: Uri) {
        if (imageUri != null) {
            id_temp_profile.setImageDrawable(null)
            selectedImageUri = imageUri
            Log.d(CLIENT_TAG, "[ProfilePictureActivity] convert Uri to bitmap for compression")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            id_temp_profile.setImageBitmap(bitmap)
        }
    }

    private fun uploadProfileImage(uid: String) {
        Log.d(CLIENT_TAG, "[ProfilePictureActivity] uploadProfileImage() to firestorage for uid: $uid")
        val ref = mStorage?.reference?.child("$ACCOUNT_DOC_PATH/$uid/profile_image")
        ref?.putFile(selectedImageUri as Uri)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    handleUploadImageSuccess(it, ref, uid)
                }
            }
    }

    private fun isImageUriExist(): Boolean {
        if (selectedImageUri != null)
            return true
        return false
    }

    private fun handleUploadImageSuccess(it: Task<UploadTask.TaskSnapshot>, ref: StorageReference, uid: String) {
        if (it.isSuccessful) {
            ref.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                mFireStore?.collection("$ACCOUNT_DOC_PATH")
                    ?.document(uid)
                    ?.update("chatUserInfo.photoUrl", "$downloadUrl")
                    ?.addOnSuccessListener {
                        Toast.makeText(this, "photoUrl saved successfully", Toast.LENGTH_LONG).show()
                        goToMainActivity(uid)
                    }
            }
        }
    }

    private fun goToMainActivity(uid: String) {
        mFireStore?.collection("$ACCOUNT_DOC_PATH")
            ?.document(uid)
            ?.get()
            ?.addOnSuccessListener {
                val user = it.toObject(User::class.java) ?: return@addOnSuccessListener

                val userType = user.userType
                var intent = Intent()

                if (userType == UserType.CLIENT) {
                    intent = Intent(this, ClientWelcomeActivity::class.java)
                } else if (userType == UserType.MECHANIC) {
                    intent = Intent(this, MechanicWelcomeActivity::class.java)
                }

                startActivity(intent)
                finish()
            }
    }
}
