package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

data class ServiceModel(
    var mechanicName: String,
    var uid: String,
    var serviceType: String,
    var price: Double,
    var description: String,
    var rating: Float
) : Parcelable {
    constructor() : this("", "", "", 0.0, "", 0.0f)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mechanicName)
        parcel.writeString(uid)
//        parcel.writeInt(serviceType.ordinal)
        parcel.writeString(serviceType)
        parcel.writeDouble(price)
        parcel.writeString(description)
        parcel.writeFloat(rating)
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