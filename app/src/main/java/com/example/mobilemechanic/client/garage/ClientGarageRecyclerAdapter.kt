package com.example.mobilemechanic.client.garage

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Vehicle
import com.squareup.picasso.Picasso
import java.util.*

class ClientGarageRecyclerAdapter(val context: Activity, val dataset: ArrayList<Vehicle>) :
    RecyclerView.Adapter<ClientGarageRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vehicleTitle = itemView.findViewById<TextView>(R.id.id_vehicle_title)
        val vehicleImage = itemView.findViewById<ImageView>(R.id.id_vehicle_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientGarageRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_vehicle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = dataset[position]
        holder.vehicleTitle.text = "${request.year} ${request.make} ${request.model}"
        if (!request.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(request.photoUrl).into(holder.vehicleImage)
        }
    }



    override fun getItemCount() = dataset.size
}