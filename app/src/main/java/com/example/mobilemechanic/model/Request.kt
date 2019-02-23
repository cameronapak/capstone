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
    var vehicle: Vehicle,
    var service: Service,
    var status: Status,
    var timePosted: Long,
    var timeCompleted: Long
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Vehicle::class.java.classLoader),
        parcel.readParcelable(Service::class.java.classLoader),
        Status.valueOf(parcel.readString()),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeString(clientId)
        parcel.writeString(mechanicId)
        parcel.writeParcelable(vehicle, flags)
        parcel.writeParcelable(service, flags)
        parcel.writeString(status.name)
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