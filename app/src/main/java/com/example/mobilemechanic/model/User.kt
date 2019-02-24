package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable
import com.example.mobilemechanic.model.dto.Address
import com.example.mobilemechanic.model.dto.BasicInfo

enum class UserType {
    CLIENT, MECHANIC
}

data class User(
    var uid: String,
    var password: String,
    var userType: UserType,
    var basicInfo: BasicInfo,
    var address: Address,
    var rating: Float
) : Parcelable {
    constructor() : this(
        "", "", UserType.CLIENT, BasicInfo(), Address(), 0f
    )

    var vehicles: ArrayList<Vehicle>? = ArrayList()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        UserType.values()[parcel.readInt()],
        parcel.readParcelable(BasicInfo::class.java.classLoader),
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readFloat()
    ) {
        arrayListOf<Vehicle>().apply {
            parcel.readList(this, Vehicle::class.java.classLoader)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(password)
        parcel.writeString(userType.name)
        parcel.writeParcelable(basicInfo, flags)
        parcel.writeParcelable(address, flags)
        parcel.writeList(vehicles)
        parcel.writeFloat(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}