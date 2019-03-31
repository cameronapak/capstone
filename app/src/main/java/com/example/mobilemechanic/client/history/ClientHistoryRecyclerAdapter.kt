package com.example.mobilemechanic.client.history

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.CLIENT_TAG
import com.example.mobilemechanic.client.detail.ServiceDetailActivity
import com.example.mobilemechanic.client.servicerating.ServiceRatingActivity
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.utility.DateTimeManager
import java.util.*

const val EXTRA_REQUEST_RATING = "extra_request_rating"

class ClientHistoryRecyclerAdapter(val context: Context, val dataset: ArrayList<Request>) :
    RecyclerView.Adapter<ClientHistoryRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.id_history_name)
        val serviceCompletedOn = itemView.findViewById<TextView>(R.id.id_service_completed)
        val description = itemView.findViewById<TextView>(R.id.id_service_description)
        val reviewButton = itemView.findViewById<Button>(R.id.id_rate_button)
        val detailsButton = itemView.findViewById<Button>(R.id.id_details_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientHistoryRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_client_history, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = dataset[position]
        var type = "${request.service?.serviceType}"
        val mechanicFirstName = request.mechanicInfo?.basicInfo?.firstName
        val mechanicLastName = request.mechanicInfo?.basicInfo?.lastName
        val vehicle = request.vehicle

        if (request.status == Status.Completed) {
            holder.serviceCompletedOn.text =
                "Completed on ${DateTimeManager.millisToDate(request.completedOn, "MMM d, y")}"
        }

        holder.name.text = "$mechanicFirstName $mechanicLastName"
        holder.description.text =
            "$type for $vehicle."

        holder.reviewButton.setOnClickListener {
            val intent = Intent(context, ServiceRatingActivity::class.java)
            intent.putExtra(EXTRA_REQUEST_RATING, request)
            Log.d(CLIENT_TAG, "[ClientHistoryRecyclerAdapter] requestID: ${request.objectID}")
            context.startActivity(intent)
        }

        holder.detailsButton.setOnClickListener {
            val intent = Intent(context, ServiceDetailActivity::class.java)
            intent.putExtra(EXTRA_REQUEST, request)
            Log.d(CLIENT_TAG, "[ClientHistoryRecyclerAdapter] requestID: ${request.objectID}")
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size
}