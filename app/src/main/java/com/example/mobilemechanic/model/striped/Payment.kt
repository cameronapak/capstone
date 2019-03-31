package com.example.mobilemechanic.model.striped

import android.os.Parcel
import android.os.Parcelable


data class Payment(
    var objectID: String,
    var amount: Double,
    var tokenId: String
) : Parcelable {
    constructor() : this("", 0.0,"")

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeString(tokenId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Payment> {
        override fun createFromParcel(parcel: Parcel): Payment {
            return Payment(parcel)
        }

        override fun newArray(size: Int): Array<Payment?> {
            return arrayOfNulls(size)
        }
    }
}