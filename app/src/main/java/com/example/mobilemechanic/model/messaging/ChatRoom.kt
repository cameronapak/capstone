package com.example.mobilemechanic.model.messaging

import android.os.Parcel
import android.os.Parcelable

const val EXTRA_CHAT_ROOM = "chat_room"

data class ChatRoom(
    var objectID: String = "",
    var clientMember: Member,
    var mechanicMember: Member
) : Parcelable
{
    constructor() : this("", Member(), Member())

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Member::class.java.classLoader),
        parcel.readParcelable(Member::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeParcelable(clientMember, flags)
        parcel.writeParcelable(mechanicMember, flags)
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

    fun getMember(myUid: String?): Member {
        return when (myUid) {
            clientMember.uid -> clientMember
            mechanicMember.uid -> mechanicMember
            else -> Member()
        }
    }

    fun getOtherMember(myUid: String?): Member {
        return when (myUid) {
            clientMember.uid -> mechanicMember
            else -> clientMember
        }
    }
}