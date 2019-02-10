package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable

enum class UserType
{
    CLIENT, MECHANIC
}

data class User(var uid: String, var email: String, var password: String, var userType: UserType, var firstName: String,
                var lastName: String, var phoneNumber: String, var address: String, var city: String, var state: String,
                var zipCode: String, var photoUrl: String) : Parcelable
{
    constructor() : this("", "", "", UserType.CLIENT, "", "", "", "",
        "", "", "", "")

    var vehicles: ArrayList<Vehicle>? = null
    var services: ArrayList<Service>? = null
    var rating: Float = 0f

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        UserType.values()[parcel.readInt()],
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )
    {
        arrayListOf<Vehicle>().apply{
            parcel.readList(this, Vehicle::class.java.classLoader)
        }
        arrayListOf<Service>().apply{
            parcel.readList(this, Service::class.java.classLoader)
        }
        rating = parcel.readFloat()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeString(uid)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeInt(userType.ordinal)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(phoneNumber)
        parcel.writeString(address)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(zipCode)
        parcel.writeString(photoUrl)
        parcel.writeList(vehicles)
        parcel.writeList(services)
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