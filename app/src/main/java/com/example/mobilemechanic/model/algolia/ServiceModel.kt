package com.example.mobilemechanic.model.algolia

import android.os.Parcel
import android.os.Parcelable
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.dto.LatLngHolder
import com.example.mobilemechanic.model.dto.MechanicInfo

data class ServiceModel(
    var objectID: String,
    var mechanicInfo: MechanicInfo,
    var service: Service,
    var _geoloc: LatLngHolder,
    var reviewCount: Int
) : Parcelable {
    constructor() : this("", MechanicInfo(), Service(), LatLngHolder(), 0)

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(MechanicInfo::class.java.classLoader),
        parcel.readParcelable(Service::class.java.classLoader),
        parcel.readParcelable(LatLngHolder::class.java.classLoader),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(objectID)
        parcel.writeParcelable(mechanicInfo, flags)
        parcel.writeParcelable(service, flags)
        parcel.writeParcelable(_geoloc, flags)
        parcel.writeInt(reviewCount)
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
        return "\nobjectID: $objectID\n" +
                "mechanicInfo: $mechanicInfo" +
                "service: $service" +
                "latlng: ${_geoloc.lat} ${_geoloc.lng}" +
                "reviewCount: $reviewCount"
    }
}