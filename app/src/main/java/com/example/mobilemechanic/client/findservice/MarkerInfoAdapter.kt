package com.example.mobilemechanic.client.findservice

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import java.util.regex.Pattern

class MarkerInfoAdapter(var context: Activity): GoogleMap.InfoWindowAdapter {

    private var markerInfoView: View? = null
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private val pattern = Pattern.compile("(\\w+):(.+)(?:\\s|\$)")

    override fun getInfoContents(marker: Marker): View? {
        var fullAddress = ""

        if (markerInfoView == null) {
            markerInfoView = inflater.inflate(R.layout.map_marker_info, null)
        }

        val title = markerInfoView?.findViewById<TextView>(R.id.id_marker_title)
        title?.text = marker.title

        val snippetPhoneNumber = markerInfoView?.findViewById<TextView>(R.id.id_snippet_phoneNumber)
        val snippetServiceType = markerInfoView?.findViewById<TextView>(R.id.id_snippet_serviceType)
        val snippetAddress = markerInfoView?.findViewById<TextView>(R.id.id_snippet_address)
        val snippetRating = markerInfoView?.findViewById<RatingBar>(R.id.id_marker_rating)
        val servicePrice = markerInfoView?.findViewById<TextView>(R.id.id_price)

        val match = pattern.matcher(marker.snippet)
        while (match.find()) {
            Log.d(CLIENT_TAG, "[MarkerInfoAdapter] group(1) ${match.group(1)}")
            Log.d(CLIENT_TAG, "[MarkerInfoAdapter] group(2) ${match.group(2)}")

            when {
                match.group(1) == "phoneNumber" ->
                    snippetPhoneNumber?.text = match.group(2)
                match.group(1) == "serviceType" ->
                    snippetServiceType?.text = match.group(2)
                match.group(1) == "addressStreet" ->
                    fullAddress = "$fullAddress${match.group(2)}\n"
                match.group(1) == "addressCityStateZipcode" ->
                    fullAddress = "$fullAddress${match.group(2)}"
                match.group(1) == "rating" ->
                    snippetRating?.rating = match.group(2).toFloat()
                match.group(1) == "servicePrice" ->
                    servicePrice?.text = match.group(2).toString()
            }
        }

        snippetAddress?.text = fullAddress
        return markerInfoView
    }

    override fun getInfoWindow(marker: Marker): View? {
        ScreenManager.hideKeyBoard(context)
        return null
    }
}