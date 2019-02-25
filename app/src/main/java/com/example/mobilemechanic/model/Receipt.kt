package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

class Receipt(
    var ojectID: String,
    var request: Request,
    var subTotal: Double,
    var estimatedTax: Double,
    var grandTottal: Double
) : Parcelable {

    constructor() : this("", Request(), 0.0, 0.0, 0.0)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Request::class.java.classLoader),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ojectID)
        parcel.writeParcelable(request, flags)
        parcel.writeDouble(subTotal)
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