package com.example.mobilemechanic.model.messaging

import android.os.Parcel
import android.os.Parcelable

const val EXTRA_CHAT_ROOM = "chat_room"

data class ChatRoom(
    var clientInfo: ChatUserInfo = ChatUserInfo(),
    var mechanicInfo: ChatUserInfo = ChatUserInfo(),
    var objectID: String = ""
) : Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(ChatUserInfo::class.java.classLoader),
        parcel.readParcelable(ChatUserInfo::class.java.classLoader),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(clientInfo, flags)
        parcel.writeParcelable(mechanicInfo, flags)
        parcel.writeString(objectID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatRoom> {
        override fun createFromParcel(parcel: Parcel): ChatRoom {
            return ChatRoom(parcel)
        }

        override fun newArray(size: Int): Array<ChatRoom?> {
            return arrayOfNulls(size)
        }
    }
}