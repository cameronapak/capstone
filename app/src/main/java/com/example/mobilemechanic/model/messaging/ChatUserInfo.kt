package com.example.mobilemechanic.model.messaging

import android.os.Parcel
import android.os.Parcelable

data class ChatUserInfo(
    var uid: String,
    var firstName: String,
    var lastName: String,
    var photoUrl: String,
    var isNewcomer: Boolean = true
) : Parcelable
{
    constructor() : this("", "", "", "", true)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt() == 1
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(photoUrl)
        parcel.writeInt(if(isNewcomer) 1 else 0)
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