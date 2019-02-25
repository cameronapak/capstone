package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

data class Service(var serviceType: String, var description: String, var price: Double) : Parcelable
{
    constructor() : this("", "", 0.0)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(serviceType)
        parcel.writeString(description)
        parcel.writeDouble(price)
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