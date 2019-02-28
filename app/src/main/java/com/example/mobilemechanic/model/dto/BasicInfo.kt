package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

class BasicInfo(
    var firstName: String,
    var lastName: String,
    var email: String,
    var phoneNumber: String,
    var photoUrl: String
) : Parcelable {
    constructor(): this("","","","","")

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeString(phoneNumber)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BasicInfo> {
        override fun createFromParcel(parcel: Parcel): BasicInfo {
            return BasicInfo(parcel)
        }

        override fun newArray(size: Int): Array<BasicInfo?> {
            return arrayOfNulls(size)
        }
    }
}