package com.example.mobilemechanic.client.garage

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.utility.DateTimeManager
import java.util.*

class VehicleHistoryRecyclerAdapter(val context: Context, val dataset: ArrayList<Request>) :
    RecyclerView.Adapter<VehicleHistoryRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.id_mechanic_name)
        val serviceCompletedOn = itemView.findViewById<TextView>(R.id.id_service_completed)
        val description = itemView.findViewById<TextView>(R.id.id_service_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleHistoryRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_vehicle_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = dataset[position]
        var type = "${request.service?.serviceType}"
        val mechanicFirstName = request.mechanicInfo?.basicInfo?.firstName
        val mechanicLastName = request.mechanicInfo?.basicInfo?.lastName

        if (request.status == Status.Completed &&
            request.completedOn != Long.MIN_VALUE) {
            holder.serviceCompletedOn.text =
                "Serviced on ${DateTimeManager.millisToDate(request.completedOn, "MMM d, y")}"
        } else {
            holder.serviceCompletedOn.text = "Date unknown"
        }

        holder.name.text = "$mechanicFirstName $mechanicLastName"
        holder.description.text =
            "$type for ${request.vehicle}"
    }

    override fun getItemCount() = dataset.size
}