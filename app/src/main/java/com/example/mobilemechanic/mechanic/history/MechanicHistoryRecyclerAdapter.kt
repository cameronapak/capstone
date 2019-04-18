package com.example.mobilemechanic.client.history

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
import com.example.mobilemechanic.mechanic.EXTRA_REQUEST
import com.example.mobilemechanic.mechanic.MECHANIC_TAG
import com.example.mobilemechanic.mechanic.detail.MechanicServiceDetailActivity
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Status
import com.example.mobilemechanic.shared.utility.DateTimeManager
import java.util.*

class MechanicHistoryRecyclerAdapter(val context: Context, val dataset: ArrayList<Request>) :
    RecyclerView.Adapter<MechanicHistoryRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.id_client_name)
        val serviceCompletedOn = itemView.findViewById<TextView>(R.id.id_service_completed)
        val description = itemView.findViewById<TextView>(R.id.id_service_description)
        val detailsButton = itemView.findViewById<Button>(R.id.id_details_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MechanicHistoryRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_mechanic_history, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = dataset[position]

        holder.name.text = "${request.clientInfo?.basicInfo?.firstName} ${request.clientInfo?.basicInfo?.lastName}"
        holder.description.text = "${request.service?.serviceType} for ${request.vehicle}"

        if (request.status == Status.Completed || request.status == Status.Paid) {
            holder.serviceCompletedOn.text =
                "Completed on ${DateTimeManager.millisToDate(request.completedOn, "MMM d, y")}"
        }

        holder.detailsButton.setOnClickListener {
            val intent = Intent(context, MechanicServiceDetailActivity::class.java)
            intent.putExtra(EXTRA_REQUEST, request)
            Log.d(MECHANIC_TAG, "[MechanicHistoryRecyclerAdapter] requestID: ${request.objectID}")
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size
}