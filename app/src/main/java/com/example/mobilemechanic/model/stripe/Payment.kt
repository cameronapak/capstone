package com.example.mobilemechanic.model.stripe

import android.os.Parcel
import android.os.Parcelable


data class Payment(
    var objectID: String,
    var amount: Double,
    var tokenId: String,
    var description: String = "[Mobile Mechanics]: "
) : Parcelable {
    constructor() : this("", 0.0,"", "[Mobile Mechanics]: ")

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeDouble(amount)
        parcel.writeString(tokenId)
        parcel.writeString(description)
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