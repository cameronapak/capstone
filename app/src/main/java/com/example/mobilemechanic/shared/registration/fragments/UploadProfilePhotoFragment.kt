package com.example.mobilemechanic.shared.registration.fragments

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.registration.RegistrationViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView

class UploadProfilePhotoFragment : Fragment() {
    private lateinit var registrationModel: RegistrationViewModel
    private var selectedImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_profile_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        activity?.let {
            registrationModel = ViewModelProviders.of(it).get(RegistrationViewModel::class.java)
        }

        setUpPager()
    }

    private fun setUpPager() {
        val pager = activity?.findViewById<ViewPager>(R.id.id_registrationPager)
        val profilePictureCircleImage = activity?.findViewById<CircleImageView>(R.id.img_registrationProfilePicture)
        val skipButton = activity?.findViewById<TextView>(R.id.btn_skipProfilePicture)
        val addressInfoButton = activity?.findViewById<Button>(R.id.btn_addressInfo)
        val backButton = activity?.findViewById<TextView>(R.id.btn_backToInfo)

        profilePictureCircleImage?.setOnClickListener {
            openGallery(it)
        }

        skipButton?.setOnClickListener {
            pager?.setCurrentItem(3, true)
        }

        addressInfoButton?.setOnClickListener {
            if(isImageUriExist(registrationModel)) {
                pager?.setCurrentItem(3, true)
            } else {
                Toast.makeText(activity, "Please choose a picture!", Toast.LENGTH_LONG).show()
            }
        }

        backButton?.setOnClickListener {
            pager?.setCurrentItem(1, true)
        }
    }

    private fun openGallery(it: View) {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setFixAspectRatio(true)
            .setAspectRatio(1, 1)
            .start(it.context, this)
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
                Toast.makeText(activity, "${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showSelectedProfileImage(imageUri: Uri) {
        if (imageUri != null) {
            val profilePictureCircleImage = activity?.findViewById<CircleImageView>(R.id.img_registrationProfilePicture)
            if (profilePictureCircleImage != null) {
                profilePictureCircleImage.setImageDrawable(null)
                selectedImageUri = imageUri
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImageUri)
                profilePictureCircleImage.setImageBitmap(bitmap)
            }
        }
    }

    private fun isImageUriExist(registrationModel: RegistrationViewModel): Boolean {
        if (selectedImageUri != null) {
            registrationModel.photoUrl.value = selectedImageUri

            return true
        }

        return false
    }

    override fun onResume() {
        super.onResume()
        val backButton = activity?.findViewById<TextView>(R.id.btn_backToInfo)
        backButton?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
}
