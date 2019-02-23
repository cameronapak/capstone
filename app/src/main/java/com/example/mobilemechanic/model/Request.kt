package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

enum class Status {
    Request, Active, Complete
}

data class Request(
    var objectID: String,
    var clientId: String,
    var mechanicId: String,
    var description: String,
    var vehicle: String,
    var service: String,
    var status: Status,
    var timePosted: Long,
    var timeCompleted: Long,
    var availableTime: String,
    var availableDay: String
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        Status.Request,
        0L,
        0L,
        "",
        ""
    )

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        Status.values()[parcel.readInt()],
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeString(clientId)
        parcel.writeString(mechanicId)
        parcel.writeString(description)
        parcel.writeString(vehicle)
        parcel.writeString(service)
        parcel.writeInt(status.ordinal)
        parcel.writeLong(timePosted)
        parcel.writeLong(timeCompleted)
        parcel.writeString(availableTime)
        parcel.writeString(availableDay)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Request> {
        override fun createFromParcel(parcel: Parcel): Request {
            return Request(parcel)
        }

        override fun newArray(size: Int): Array<Request?> {
            return arrayOfNulls(size)
        }
    }
}