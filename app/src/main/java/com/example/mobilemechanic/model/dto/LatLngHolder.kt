package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

class LatLngHolder(var lat: Double, var lng: Double): Parcelable {

    constructor(): this(0.0, 0.0)

    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LatLngHolder> {
        override fun createFromParcel(parcel: Parcel): LatLngHolder {
            return LatLngHolder(parcel)
        }

        override fun newArray(size: Int): Array<LatLngHolder?> {
            return arrayOfNulls(size)
        }
    }

}