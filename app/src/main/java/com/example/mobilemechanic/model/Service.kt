package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

data class Service(var serviceType: String, var price: Double, var comment: String) : Parcelable
{
    constructor() : this("", 0.0, "")

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(serviceType)
        parcel.writeDouble(price)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Service> {
        override fun createFromParcel(parcel: Parcel): Service {
            return Service(parcel)
        }

        override fun newArray(size: Int): Array<Service?> {
            return arrayOfNulls(size)
        }
    }
}