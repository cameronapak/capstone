package com.example.mobilemechanic.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri

class RegistrationViewModel : ViewModel() {
    var userType = MutableLiveData<UserType>()
    var emailAddress = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var phoneNumber = MutableLiveData<String>()
    var imageUri = MutableLiveData<Uri>()
    var streetAddress = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var state = MutableLiveData<String>()
    var zipcode = MutableLiveData<String>()
}