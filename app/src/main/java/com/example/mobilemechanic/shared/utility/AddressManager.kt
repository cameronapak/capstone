package com.example.mobilemechanic.shared.utility

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.example.mobilemechanic.model.dto.Address
import com.example.mobilemechanic.model.dto.LatLngHolder
import com.google.android.gms.maps.model.LatLng
import java.util.*

object AddressManager
{

    private var address: Address? = null

    fun saveUserAddress(addr: Address) {
        address = addr
    }

    fun getUserAddress(): Address? {
        return address
    }

    fun hasAddress() : Boolean {
        return address != null
    }

    fun convertAddressToLatLng(context: Context, address: Address?) : LatLng
    {
        val geoCoder = Geocoder(context, Locale.US)
        val addressList = geoCoder.getFromLocationName(address.toString(), 1)
        return if (addressList.size > 0)
            LatLng(addressList[0].latitude, addressList[0].longitude)
        else LatLng(35.656017, -97.473716)
    }

    fun convertAddressToLatLngHolder(context: Context?, address: Address?) : LatLngHolder
    {
        val geoCoder = Geocoder(context, Locale.US)
        val addressList = geoCoder.getFromLocationName(address.toString(), 1)
        return if (addressList.size > 0)
            LatLngHolder(addressList[0].latitude, addressList[0].longitude)
        else LatLngHolder(35.656017, -97.473716)
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

    fun getDistanceMI(clientLatLng: LatLngHolder, mechanicLatLng: LatLngHolder) : Double
    {
        val clientLocation = Location("Client").apply {
            latitude = clientLatLng.lat
            longitude = clientLatLng.lng
        }

        val mechanicLocation = Location("Mechanic").apply {
            latitude = mechanicLatLng.lat
            longitude = mechanicLatLng.lng
        }

        return (mechanicLocation.distanceTo(clientLocation).toDouble() / 1000) / 1.609
    }

    fun getDistanceMI(clientLocation: Location, mechanicLocation: Location) : Double
    {
        return (mechanicLocation.distanceTo(clientLocation).toDouble() / 1000) / 1.609
    }
}