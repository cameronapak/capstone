package com.example.mobilemechanic.shared.utility

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.example.mobilemechanic.model.dto.Address
import com.google.android.gms.maps.model.LatLng

object AddressManager
{
    fun getFullAddress(address: Address) = "${address.street} ${address.city}, ${address.state} ${address.zipCode}"

    fun convertAddress(context: Context, address: String) : LatLng
    {
        val geoCoder = Geocoder(context)
        val addressList = geoCoder.getFromLocationName(address, 1)
        return LatLng(addressList[0].latitude, addressList[0].longitude)
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