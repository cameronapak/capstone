package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

class User(var userType: String,
           var email: String,
           var password: String,
           var passwordConfirmation: String,
           var firstName: String,
           var lastName: String,
           var phoneNumber: String,
           var address: String,
           var city: String,
           var state: String,
           var zip: String) : Parcelable {

    constructor() : this("", "", "", "", "", "", "", "", "", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userType)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(passwordConfirmation)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(phoneNumber)
        parcel.writeString(address)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(zip)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}