package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

class Receipt(
    var tips: Double,
    var subTotal: Double,
    var estimatedTax: Double,
    var grandTottal: Double
) : Parcelable {

    constructor() : this(0.0, 0.0, 0.0, 0.0)
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(subTotal)
        parcel.writeDouble(tips)
        parcel.writeDouble(estimatedTax)
        parcel.writeDouble(grandTottal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Receipt> {
        override fun createFromParcel(parcel: Parcel): Receipt {
            return Receipt(parcel)
        }

        override fun newArray(size: Int): Array<Receipt?> {
            return arrayOfNulls(size)
        }
    }
}