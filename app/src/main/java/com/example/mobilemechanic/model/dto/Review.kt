package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

data class Review(
    var rating: Float,
    var comment: String,
    var whatWentWrong: ArrayList<String>,
    var postedOn: Long
) : Parcelable {
    constructor() : this(
        0F,
        "",
        ArrayList<String>(),
        Long.MIN_VALUE
    )

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readString(),
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(rating)
        parcel.writeString(comment)
        parcel.writeList(whatWentWrong)
        parcel.writeLong(postedOn)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}