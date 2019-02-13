package com.example.mobilemechanic.client.history

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.servicerating.ServiceRatingActivity
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.ServiceType

class ClientHistoryRecyclerAdapter(val context: Context, val dataset: ArrayList<Request>) :
    RecyclerView.Adapter<ClientHistoryRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.id_history_name)
        val serviceProgress = itemView.findViewById<TextView>(R.id.id_service_status)
        val description = itemView.findViewById<TextView>(R.id.id_service_description)
        val rateButton = itemView.findViewById<Button>(R.id.id_rate_button)
        val detailsButton = itemView.findViewById<Button>(R.id.id_details_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientHistoryRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_client_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = dataset[position]
        var type = ""

        if(request.isComplete) holder.serviceProgress.text = "Service Completed"
        else holder.serviceProgress.text = "Service in Progress"

        if(request.service.serviceType == ServiceType.OIL_CHANGE) type = "Oil Change"
        else if(request.service.serviceType == ServiceType.CHECK_ENGINE_LIGHT) type = "Check Engine Light"
        else if(request.service.serviceType == ServiceType.FILL_GAS) type = "Fill Gas"
        else if(request.service.serviceType == ServiceType.TIRE_CHANGE) type = "Tire Change"

        holder.name.text = "${request.mechanicId}"
        holder.description.text = "${type} for ${request.vehicle.year} ${request.vehicle.make} ${request.vehicle.model}"


        holder.rateButton.setOnClickListener {

            val intent = Intent(context, ServiceRatingActivity::class.java)
            intent.putExtra("name", "${request.mechanicId}")
            context.startActivity(intent)

        }

        holder.detailsButton.setOnClickListener {

        }
    }

    override fun getItemCount() = dataset.size


}