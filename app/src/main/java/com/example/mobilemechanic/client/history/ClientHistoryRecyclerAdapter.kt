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
import com.example.mobilemechanic.client.detail.ServiceDetailActivity
import com.example.mobilemechanic.client.servicerating.ServiceRatingActivity
import com.example.mobilemechanic.model.Receipt
import com.example.mobilemechanic.model.Status

class ClientHistoryRecyclerAdapter(val context: Context, val dataset: ArrayList<Receipt>) :
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
        val receipt = dataset[position]
        var type = "${receipt.request.service?.serviceType}"

        if (receipt.request.status == Status.Complete) {
            holder.serviceProgress.text = "Service Completed"
        }

        holder.name.text = "${receipt.request.mechanicInfo?.basicInfo?.firstName} ${receipt.request.mechanicInfo?.basicInfo?.lastName}"
        holder.description.text =
            "$type for ${receipt.request.vehicle?.year} ${receipt.request.vehicle?.make} ${receipt.request.vehicle?.model}"

        holder.rateButton.setOnClickListener {
            val intent = Intent(context, ServiceRatingActivity::class.java)
            intent.putExtra("request", receipt.request)
            context.startActivity(intent)
        }

        holder.detailsButton.setOnClickListener {
            context.startActivity(Intent(context, ServiceDetailActivity::class.java))
        }
    }

    override fun getItemCount() = dataset.size


}