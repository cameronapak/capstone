package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

data class Vehicle(var objectID: String, var year: String, var make: String, var model: String, var photoUrl: String) : Parcelable
{
    constructor() : this("", "", "", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeString(year)
        parcel.writeString(make)
        parcel.writeString(model)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vehicle> {
        override fun createFromParcel(parcel: Parcel): Vehicle {
            return Vehicle(parcel)
        }

        override fun newArray(size: Int): Array<Vehicle?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "$year $make $model"
    }
}