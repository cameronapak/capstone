package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

enum class Status
{
    Request, Active, Complete
}

data class Request(var clientId: String, var mechanicId: String, var description: String, var vehicle: Vehicle,
                   var service: Service, var status: Status, var timePosted: Long, var timeCompleted: Long) : Parcelable
{
    constructor() : this("", "", "",
        Vehicle(), Service(), Status.Request, 0L, 0L)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readTypedObject(Vehicle),
        parcel.readTypedObject(Service),
        Status.values()[parcel.readInt()],
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(clientId)
        parcel.writeString(mechanicId)
        parcel.writeString(description)
        parcel.writeTypedObject(vehicle, flags)
        parcel.writeTypedObject(service, flags)
        parcel.writeInt(status.ordinal)
        parcel.writeLong(timePosted)
        parcel.writeLong(timeCompleted)
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