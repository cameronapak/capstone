package com.example.mobilemechanic.shared.Registration.fragments

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.shared.Registration.RegistrationViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_upload_profile_photo.*

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

        img_registrationProfilePicture.setOnClickListener {
            openGallery(it)
        }

        btn_skipProfilePicture.setOnClickListener {
            pager?.setCurrentItem(3, true)
        }

        btn_addressInfo.setOnClickListener {
            if(isImageUriExist(registrationModel)) {
                pager?.setCurrentItem(3, true)
            } else {
                Toast.makeText(activity, "Please choose a picture!", Toast.LENGTH_LONG).show()
            }
        }

        btn_backToInfo.setOnClickListener {
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
            id_tempRegistrationProfile.setImageDrawable(null)
            selectedImageUri = imageUri
            Log.d(CLIENT_TAG, "[ProfilePictureActivity] convert Uri to bitmap for compression")
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImageUri)
            id_tempRegistrationProfile.setImageBitmap(bitmap)
        }
    }

    private fun isImageUriExist(registrationModel: RegistrationViewModel): Boolean {
        if (selectedImageUri != null) {
            registrationModel.imageUri.value = selectedImageUri

            return true
        }

        return false
    }
}
