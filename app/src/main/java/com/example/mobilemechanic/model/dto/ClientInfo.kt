package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

data class ClientInfo(
    var uid: String,
    var basicInfo: BasicInfo,
    var availability: Availability,
    var address: Address
) : Parcelable {
    constructor() : this(
        "",
        BasicInfo(),
        Availability(),
        Address())

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(BasicInfo::class.java.classLoader),
        parcel.readParcelable(Availability::class.java.classLoader),
        parcel.readParcelable(Address::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeParcelable(basicInfo, flags)
        parcel.writeParcelable(availability, flags)
        parcel.writeParcelable(address, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClientInfo> {
        override fun createFromParcel(parcel: Parcel): ClientInfo {
            return ClientInfo(parcel)
        }

        override fun newArray(size: Int): Array<ClientInfo?> {
            return arrayOfNulls(size)
        }
    }
}