package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

enum class ServiceType
{
    CHECK_ENGINE_LIGHT, FILL_GAS, OIL_CHANGE, TIRE_CHANGE
}

data class Service(var serviceType: ServiceType, var price: Double, var comment: String) : Parcelable
{
    constructor() : this(ServiceType.CHECK_ENGINE_LIGHT, 0.0, "")

    constructor(parcel: Parcel) : this(
        ServiceType.values()[parcel.readInt()],
        parcel.readDouble(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(serviceType.ordinal)
        parcel.writeDouble(price)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Service> {
        override fun createFromParcel(parcel: Parcel): Service {
            return Service(parcel)
        }

        override fun newArray(size: Int): Array<Service?> {
            return arrayOfNulls(size)
        }
    }
}