package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

class Availability(
    var fromTime: String,
    var toTime: String,
    var days: ArrayList<String> = ArrayList()
) : Parcelable {
    constructor() : this("", "", ArrayList<String>())

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fromTime)
        parcel.writeString(toTime)
        parcel.writeList(days)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Availability> {
        override fun createFromParcel(parcel: Parcel): Availability {
            return Availability(parcel)
        }

        override fun newArray(size: Int): Array<Availability?> {
            return arrayOfNulls(size)
        }
    }

}