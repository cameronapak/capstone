package com.example.mobilemechanic.client.garage

import android.os.Parcel
import android.os.Parcelable

class VechicleInformation(var theYear:Int, var theModel: String, var theMake:String):
    Parcelable{


    constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readString(),
    parcel.readString()

    ) {
    }


    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeInt(theYear)
        parcel?.writeString(theModel)
        parcel?.writeString(theMake)
    }

    override fun describeContents(): Int {
         return 0;
    }

    companion object CREATOR : Parcelable.Creator<VechicleInformation> {
        override fun createFromParcel(parcel: Parcel): VechicleInformation {
            return VechicleInformation(parcel)
        }

        override fun newArray(size: Int): Array<VechicleInformation?> {
            return arrayOfNulls(size)
        }
    }

}