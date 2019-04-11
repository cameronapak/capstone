package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable
import com.example.mobilemechanic.model.dto.ClientInfo
import com.example.mobilemechanic.model.dto.MechanicInfo

enum class Status {
    Pending, Request, Active, Accepted, Completed, Cancelled, Declined
}

data class Request(
    var objectID: String,
    var clientInfo: ClientInfo?,
    var mechanicInfo: MechanicInfo?,
    var service: Service?,
    var vehicle: Vehicle?,
    var comment: String?,
    var status: Status?,
    var postedOn: Long?,
    var acceptedOn: Long?,
    var completedOn: Long?,
    var receipt: String?
) : Parcelable {
    constructor() : this(
        "",
        ClientInfo(),
        MechanicInfo(),
        Service(),
        Vehicle(),
        "",
        Status.Request,
        Long.MIN_VALUE,
        Long.MIN_VALUE,
        Long.MIN_VALUE,
        ""
    )

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(ClientInfo::class.java.classLoader),
        parcel.readParcelable(MechanicInfo::class.java.classLoader),
        parcel.readParcelable(Service::class.java.classLoader),
        parcel.readParcelable(Vehicle::class.java.classLoader),
        parcel.readString(),
        Status.valueOf(parcel.readString()),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeParcelable(clientInfo, flags)
        parcel.writeParcelable(mechanicInfo, flags)
        parcel.writeParcelable(service, flags)
        parcel.writeParcelable(vehicle, flags)
        parcel.writeString(comment)
        parcel.writeString(status?.name)
        postedOn?.let { parcel.writeLong(it) }
        acceptedOn?.let { parcel.writeLong(it) }
        completedOn?.let { parcel.writeLong(it) }
        parcel.writeString(receipt)
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

    data class Builder(
        var objectID: String? = null,
        var clientInfo: ClientInfo? = null,
        var mechanicInfo: MechanicInfo? = null,
        var service: Service? = null,
        var vehicle: Vehicle? = null,
        var comment: String? = null,
        var status: Status? = null,
        var postedOn: Long? = Long.MIN_VALUE,
        var acceptedOn: Long? = Long.MIN_VALUE,
        var completedOn: Long? = Long.MIN_VALUE,
        var receipt: String? = null
    ) {
        fun clientInfo(info: ClientInfo) = apply { this.clientInfo = info }
        fun mechanicInfo(info: MechanicInfo) = apply { this.mechanicInfo = info }
        fun service(s: Service) = apply { this.service = s }
        fun vehicle(v: Vehicle) = apply { this.vehicle = v }
        fun comment(c: String) = apply { this.comment = c }
        fun status(status: Status) = apply { this.status = status }
        fun postedOn(posted: Long) = apply { this.postedOn = posted }
        fun acceptedOn(accepted: Long) = apply { this.acceptedOn = accepted }
        fun completedOn(completed: Long) = apply { this.completedOn = completed }
        fun receipt(receipt: String) = apply { this.receipt = receipt }
        fun build() =
            Request(
                "",
                clientInfo,
                mechanicInfo,
                service,
                vehicle,
                comment,
                status,
                postedOn,
                acceptedOn,
                completedOn,
                receipt
            )
    }
}
