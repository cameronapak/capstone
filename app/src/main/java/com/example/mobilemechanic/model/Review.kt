package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable
import com.example.mobilemechanic.model.dto.ClientInfo
import com.example.mobilemechanic.model.dto.MechanicInfo

data class Review(
    var clientInfo: ClientInfo?,
    var mechanicInfo: MechanicInfo?,
    var review: String?,
    var tag: String?,
    var rating: Float?

) : Parcelable {
    constructor() : this(
        ClientInfo(),
        MechanicInfo(),
        "",
        "",
        0F
    )

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(ClientInfo::class.java.classLoader),
        parcel.readParcelable(MechanicInfo::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(clientInfo, flags)
        parcel.writeParcelable(mechanicInfo, flags)
        parcel.writeString(review)

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


    data class Builder(
        var clientInfo: ClientInfo? = null,
        var mechanicInfo: MechanicInfo? = null,
        var review: String? = null,
        var tag: String? = null,
        var rating: Float? = 0F
    ) {
        fun clientInfo(info: ClientInfo) = apply { this.clientInfo = info }
        fun mechanicInfo(info: MechanicInfo) = apply { this.mechanicInfo = info }
        fun review(r: String) = apply { this.review = r }
        fun tag(t: String) = apply { this.tag = t }
        fun rating(rate: Float) = apply {this.rating = rate}

        fun build() =
            Review(clientInfo, mechanicInfo, review, tag, rating)
    }
}