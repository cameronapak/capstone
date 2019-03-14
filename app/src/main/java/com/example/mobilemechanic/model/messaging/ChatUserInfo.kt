package com.example.mobilemechanic.model.messaging

import android.os.Parcel
import android.os.Parcelable

data class ChatUserInfo(var uid: String, var firstName: String, var lastName: String, var photoUrl: String) : Parcelable
{
    constructor() : this("", "", "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatUserInfo> {
        override fun createFromParcel(parcel: Parcel): ChatUserInfo {
            return ChatUserInfo(parcel)
        }

        override fun newArray(size: Int): Array<ChatUserInfo?> {
            return arrayOfNulls(size)
        }
    }
}