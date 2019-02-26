package com.example.mobilemechanic.shared

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.mobilemechanic.R
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
import com.theartofdev.edmodo.cropper.CropImageActivity
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_profile_picture.*
import java.security.AccessController.getContext

const val PICK_FROM_GALLERY = 101

class ProfilePictureActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?= null
    private var mFireStore: FirebaseFirestore? = null
    private var mStorage: FirebaseStorage? = null
    private var fileUriPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture)

        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        mStorage = FirebaseStorage.getInstance()
        val user = mAuth?.currentUser

        if(user != null) {
            mFireStore?.collection(ACCOUNT_DOC_PATH)
                ?.document(user.uid)
                ?.get()
                ?.addOnSuccessListener {
                    val users = it.toObject(User::class.java)
                    if (users == null) return@addOnSuccessListener

                    val userType = users.userType

                    Toast.makeText(this, "$userType", Toast.LENGTH_SHORT).show()
                }
        }

        img_profilePicture.setOnClickListener {
            openGallery()

            /*CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)*/
        }

        btn_uploadProfilePicture.setOnClickListener {
            Toast.makeText(this, "$fileUriPath", Toast.LENGTH_SHORT).show()

            if(isUploadFileExist()) {
                uploadProfileImageToFirebaseStorage()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        startActivityForResult(Intent.createChooser(intent, "Complete action using"),PICK_FROM_GALLERY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_GALLERY
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null) {

            showSelectedProfileImage(data.data)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            fileUriPath = result.uri
            img_profilePicture.setImageURI(fileUriPath)
        }
    }

    private fun showSelectedProfileImage(fileUri: Uri) {
        fileUriPath = fileUri
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUriPath)
        img_profilePicture.setImageBitmap(bitmap)
    }

    private fun uploadProfileImageToFirebaseStorage() {


            //val currentUser = mAuth?.currentUser

            //if (currentUser != null) {
                //val uid = currentUser.uid

                val uid = "l1laJN6sgZP89ODvqt5CquXfRE33"

                Toast.makeText(this, "$uid", Toast.LENGTH_SHORT).show()

                val ref = mStorage?.reference?.child("$ACCOUNT_DOC_PATH/$uid")
                ref?.putFile(fileUriPath as Uri)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            handleUploadImageSuccess(it, ref, uid)
                        }
                    }

        //}

        Toast.makeText(this, "NO USER", Toast.LENGTH_SHORT).show()
    }

    private fun isUploadFileExist(): Boolean {
        if (fileUriPath != null)
            return true
        return false
    }

    private fun handleUploadImageSuccess(it: Task<UploadTask.TaskSnapshot>, ref: StorageReference, uid: String) {
        if (it.isSuccessful) {
            ref.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                val uploadImageFile = "basicInfo/photoUrl/$downloadUrl"
                mFireStore?.collection("$ACCOUNT_DOC_PATH")
                    ?.document(uid)
                    ?.set(uploadImageFile)
                    ?.addOnSuccessListener {
                        mFireStore?.collection(ACCOUNT_DOC_PATH)
                            ?.document(uid)
                            ?.get()
                            ?.addOnSuccessListener {
                                val user = it.toObject(User::class.java)
                                if (user == null) return@addOnSuccessListener

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
        }
    }
}
