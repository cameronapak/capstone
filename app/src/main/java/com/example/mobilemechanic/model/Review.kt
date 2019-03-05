package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable
import com.example.mobilemechanic.model.dto.MechanicInfo

data class Review(
    var objectID: String,
    var rating: Float,
    var comment: String,
    var whatWentWrong: ArrayList<String>,
    var postedOn: Long,
    var requestID: String,
    var mechanicInfo: MechanicInfo?
) : Parcelable {
    constructor() : this(
        "",
        0F,
        "",
        ArrayList<String>(),
        Long.MIN_VALUE,
        "",
        MechanicInfo()
    )

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readFloat(),
        parcel.readString(),
        arrayListOf<String>().apply {
            parcel.readList(this, String::class.java.classLoader)
        },
        parcel.readLong(),
        parcel.readString(),
        parcel.readParcelable(MechanicInfo::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeFloat(rating)
        parcel.writeString(comment)
        parcel.writeList(whatWentWrong)
        parcel.writeLong(postedOn)
        parcel.writeString(requestID)
        parcel.writeParcelable(mechanicInfo, flags)
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