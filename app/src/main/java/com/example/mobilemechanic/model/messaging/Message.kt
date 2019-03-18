package com.example.mobilemechanic.model.messaging

import android.os.Parcel
import android.os.Parcelable

data class Message(var chatUserInfo: Member,
                   var contents: String,
                   var timeStamp: Long
) : Parcelable
{
    constructor() : this(Member(), "", 0L)

    constructor(parcel: Parcel) : this(
        parcel.readParcelable<Member>(Member::class.java.classLoader),
        parcel.readString(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(chatUserInfo, flags)
        parcel.writeString(contents)
        parcel.writeLong(timeStamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}
