package com.example.mobilemechanic.shared.utility

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.example.mobilemechanic.model.dto.Address
import com.google.android.gms.maps.model.LatLng
import java.util.*

object AddressManager
{

//    fun getFullAddress(address: Address) = "${address.street} ${address.city}, ${address.state} ${address.zipCode}"

    fun convertAddressToLatLng(context: Context, address: Address?) : LatLng
    {
        val geoCoder = Geocoder(context, Locale.US)
        val addressList = geoCoder.getFromLocationName(address.toString(), 1)
        return if (addressList.size > 0) LatLng(addressList[0].latitude, addressList[0].longitude)
        else LatLng(35.656017, -97.473716)
    }

    fun getDistanceKM(clientLatLng: LatLng, mechanicLatLng: LatLng) : Double
    {
        val clientLocation = Location("Client")
        clientLocation.latitude = clientLatLng.latitude
        clientLocation.longitude = clientLatLng.longitude

        val mechanicLocation = Location("Mechanic")
        mechanicLocation.latitude = mechanicLatLng.latitude
        mechanicLocation.longitude = mechanicLatLng.longitude

        return mechanicLocation.distanceTo(clientLocation).toDouble() / 1000
    }

    fun getDistanceMI(clientLatLng: LatLng, mechanicLatLng: LatLng) : Double
    {
        val clientLocation = Location("Client")
        clientLocation.latitude = clientLatLng.latitude
        clientLocation.longitude = clientLatLng.longitude

        val mechanicLocation = Location("Mechanic")
        mechanicLocation.latitude = mechanicLatLng.latitude
        mechanicLocation.longitude = mechanicLatLng.longitude

        return (mechanicLocation.distanceTo(clientLocation).toDouble() / 1000) / 1.609
    }
}