package com.example.mobilemechanic.model

import android.os.Parcel
import android.os.Parcelable
import com.example.mobilemechanic.model.dto.BasicInfo

data class Message(var yourInfo: BasicInfo, var theirInfo: BasicInfo, var senderId: String, var recipientId: String, var contents: String, var timeStamp: Long) : Parcelable
{
    constructor() : this(BasicInfo(),BasicInfo(),"", "", "", 0L)

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BasicInfo::class.java.classLoader),
        parcel.readParcelable(BasicInfo::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(yourInfo, flags)
        parcel.writeParcelable(theirInfo, flags)
        parcel.writeString(senderId)
        parcel.writeString(recipientId)
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
