package com.algolia.instantsearch.examples.icebnb.widgets

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.algolia.instantsearch.core.helpers.Searcher
import com.algolia.instantsearch.core.model.AlgoliaResultsListener
import com.algolia.instantsearch.core.model.AlgoliaSearcherListener
import com.algolia.instantsearch.core.model.SearchResults
import com.algolia.search.saas.Query
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.findservice.EXTRA_SERVICE
import com.example.mobilemechanic.client.findservice.MarkerInfoAdapter
import com.example.mobilemechanic.client.postservicerequest.PostServiceRequestActivity
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.regex.Pattern


class MapWidget(var context: Activity, mapFragment: SupportMapFragment) : OnMapReadyCallback, AlgoliaSearcherListener,
    AlgoliaResultsListener, GoogleMap.OnMarkerClickListener {

    var googleMap: GoogleMap? = null
    private val hits = ArrayList<JSONObject>()
    private var servicesHits: ArrayList<ServiceModel> = ArrayList()
    private var gson: Gson
    private var customerMarkerView: View
    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        mapFragment.getMapAsync(this)
        gson = Gson()
        customerMarkerView = LayoutInflater.from(context).inflate(R.layout.map_marker, null)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setInfoWindowAdapter(MarkerInfoAdapter(context))
        googleMap.setOnInfoWindowClickListener { marker ->
            val pattern = Pattern.compile("(\\w+):(.+)(?:\\s|\$)")
            val match = pattern.matcher(marker.snippet)
            var serviceId = ""
            while (match.find()) {
                Log.d(CLIENT_TAG, "[MarkerInfoAdapter] group(1) ${match.group(1)}")
                Log.d(CLIENT_TAG, "[MarkerInfoAdapter] group(2) ${match.group(2)}")

                when {
                    match.group(1) == "serviceObjectID" ->
                        serviceId = match.group(2).toString()
                }
            }

            if (!serviceId.isNullOrEmpty()) {
                mFirestore.collection("Services").document(serviceId).get()
                    .addOnSuccessListener {
                        val serviceObj = it.toObject(ServiceModel::class.java)
                        val intent = Intent(context, PostServiceRequestActivity::class.java)
                        intent.putExtra(EXTRA_SERVICE, serviceObj)
                        context.startActivity(intent)
                    }
            }
        }
    }

    override fun onResults(results: SearchResults, isLoadingMore: Boolean) {
        addHits(results, !isLoadingMore)
        if (googleMap != null) {
            googleMap!!.setOnMapLoadedCallback { updateMap() }
        }
    }

    private fun addHits(results: SearchResults?, isReplacing: Boolean) {
        if (results == null) {
            if (isReplacing) {
                hits.clear()
            }
            return
        }
        val newHits = results.hits
        if (isReplacing) {
            hits.clear()
        }

        for (i in 0 until newHits.length()) {
            val hit = newHits.optJSONObject(i)
            if (hit != null) {
                hits.add(hit)
            }
        }
    }

    private fun updateMap() {
        servicesHits.clear()
        googleMap!!.clear()
        if (hits.isEmpty()) {
            return
        }

        val type = object : TypeToken<ArrayList<ServiceModel>>() {}.type
        servicesHits = gson.fromJson(hits.toString(), type)

        servicesHits
            .distinctBy { it.mechanicInfo.uid }
            .forEachIndexed { index, serviceModel ->
                Picasso.get().load(Uri.parse(serviceModel.mechanicInfo.basicInfo.photoUrl)).into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {

                        val mechanicBasicInfo = serviceModel.mechanicInfo.basicInfo
                        val address = serviceModel.mechanicInfo.address
                        val snippetText =
                            "phoneNumber:${mechanicBasicInfo.phoneNumber}\n" +
                                    "serviceType:${serviceModel.service.serviceType}\n" +
                                    "addressStreet:${address.street}\n" +
                                    "addressCityStateZipcode:${address.city}, ${address.state} ${address.zipCode}\n" +
                                    "rating:${serviceModel.mechanicInfo.rating}\n" +
                                    "serviceObjectID:${serviceModel.objectID}\n" +
                                    "servicePrice:${serviceModel.service.price}"

                        googleMap!!.addMarker(
                            MarkerOptions()
                                .position(LatLng(serviceModel._geoloc.lat, serviceModel._geoloc.lng))
                                .title("${mechanicBasicInfo.firstName} ${mechanicBasicInfo.lastName}")
                                .snippet(snippetText)
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(bitmap)))
                        )
                    }
                })

                if (index == 0) {
                    cameraZoomToMostRelevantService(serviceModel)
                }
            }
    }

    private fun cameraZoomToMostRelevantService(serviceModel: ServiceModel) {
        val cameraUpdate = CameraUpdateFactory
            .newLatLng(LatLng(serviceModel._geoloc.lat, serviceModel._geoloc.lng))
        googleMap!!.moveCamera(cameraUpdate)
        googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(10F), 2000, null)
    }

    private fun getMarkerBitmapFromView(bitmap: Bitmap): Bitmap {
        val markerProfileImage = customerMarkerView.findViewById<CircleImageView>(R.id.id_marker_profile_image)
        markerProfileImage.setImageBitmap(bitmap)
        customerMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customerMarkerView.layout(0, 0, customerMarkerView.measuredWidth, customerMarkerView.measuredHeight)
        customerMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customerMarkerView.measuredWidth,
            customerMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        customerMarkerView.background?.draw(canvas)
        customerMarkerView.draw(canvas)
        return returnedBitmap
    }

    override fun initWithSearcher(searcher: Searcher) {
        searcher.query = searcher.query.setAroundRadius(Query.RADIUS_ALL)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }
}
