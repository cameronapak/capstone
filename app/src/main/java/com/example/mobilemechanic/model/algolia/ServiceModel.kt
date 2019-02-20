package com.example.mobilemechanic.model.algolia

import android.os.Parcel
import android.os.Parcelable

data class ServiceModel(
    var objectID: String,
    var mechanicFirstName: String,
    var mechanicLastName: String,
    var mechanicPhotoUrl: String,
    var uid: String,
    var serviceType: String,
    var price: Double,
    var description: String,
    var rating: Float
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        0.0,
        "",
        0.0f)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeString(mechanicFirstName)
        parcel.writeString(mechanicLastName)
        parcel.writeString(mechanicPhotoUrl)
        parcel.writeString(uid)
        parcel.writeString(serviceType)
        parcel.writeDouble(price)
        parcel.writeString(description)
        parcel.writeFloat(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServiceModel> {
        override fun createFromParcel(parcel: Parcel): ServiceModel {
            return ServiceModel(parcel)
        }

        override fun newArray(size: Int): Array<ServiceModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return  "\nobjectID: $objectID\n" +
                "mechanicFirstName: $mechanicFirstName\n" +
                "mechanicLastName: $mechanicLastName\n" +
                "mechanicPhotoUrl: $mechanicPhotoUrl\n" +
                "uid: $uid\n" +
                "serviceType: $serviceType\n" +
                "price: $price\n" +
                "description: $description\n" +
                "rating: $rating\n"
    }
}