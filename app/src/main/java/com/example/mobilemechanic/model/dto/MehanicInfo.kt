package com.example.mobilemechanic.model.dto

import android.os.Parcel
import android.os.Parcelable

data class MechanicInfo(
    var uid: String,
    var basicInfo: BasicInfo,
    var address: Address,
    var rating: Float
) : Parcelable {
    constructor() : this("", BasicInfo(), Address(), 0f)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(BasicInfo::class.java.classLoader),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeParcelable(basicInfo, flags)
        parcel.writeParcelable(address, flags)
        parcel.writeFloat(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MechanicInfo> {
        override fun createFromParcel(parcel: Parcel): MechanicInfo {
            return MechanicInfo(parcel)
        }

        override fun newArray(size: Int): Array<MechanicInfo?> {
            return arrayOfNulls(size)
        }
    }
}