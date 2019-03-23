package com.example.mobilemechanic.shared.registration

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.example.mobilemechanic.model.UserType

class RegistrationViewModel : ViewModel() {
    var userType = MutableLiveData<UserType>()
    var emailAddress = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var phoneNumber = MutableLiveData<String>()
    var photoUrl = MutableLiveData<Uri>()
    var streetAddress = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var state = MutableLiveData<String>()
    var zipcode = MutableLiveData<String>()
}