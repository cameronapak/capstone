package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

data class ClientInfo(
    var uid: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var phoneNumber: String,
    var photoUrl: String,
    var availability: Availability,
    var address: Address
) : Parcelable {
    constructor() : this("", "", "", "", "", "", Availability(), Address())

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Availability::class.java.classLoader),
        parcel.readParcelable(Address::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(phoneNumber)
        parcel.writeString(photoUrl)
        parcel.writeParcelable(availability, flags)
        parcel.writeParcelable(address, flags)
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