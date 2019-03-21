package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

data class Address(
    var street: String,
    var city: String,
    var state: String,
    var zipCode: String,
    var _geoloc: LatLngHolder
) : Parcelable {
    constructor() : this("", "", "", "", LatLngHolder())

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(LatLngHolder::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(street)
        parcel.writeString(state)
        parcel.writeString(city)
        parcel.writeString(zipCode)
        parcel.writeParcelable(_geoloc, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "$street $city, $state $zipCode"
    }
}