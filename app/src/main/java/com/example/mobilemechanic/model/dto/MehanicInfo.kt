package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

data class MechanicInfo(
    var uid: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var photoUrl: String,
    var address: Address,
    var rating: Float
) : Parcelable {
    constructor() : this("", "", "", "", "", "", Address(), 0f)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(phoneNumber)
        parcel.writeString(photoUrl)
        parcel.writeParcelable(address, flags)
        parcel.writeFloat(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClientInfo> {
        override fun createFromParcel(parcel: Parcel): ClientInfo {
            return ClientInfo(parcel)
        }

        override fun newArray(size: Int): Array<ClientInfo?> {
            return arrayOfNulls(size)
        }
    }
}