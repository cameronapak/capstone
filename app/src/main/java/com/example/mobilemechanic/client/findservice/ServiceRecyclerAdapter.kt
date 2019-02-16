package com.example.mobilemechanic.client.findservice

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.postservicerequest.PostServiceRequestActivity
import com.example.mobilemechanic.model.ServiceModel

const val EXTRA_SERVICE = "extra_service"

class ServiceRecyclerAdapter(val context: Context, val dataset: ArrayList<ServiceModel>) :
    RecyclerView.Adapter<ServiceRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.id_service_type)
        val description = itemView.findViewById<TextView>(R.id.id_description)
        val name = itemView.findViewById<TextView>(R.id.id_mechanic_name)
        val selectButton = itemView.findViewById<Button>(R.id.id_select_button)
        val price = itemView.findViewById<TextView>(R.id.id_service_price)
        val rating = itemView.findViewById<TextView>(R.id.id_mechanic_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_service, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = dataset[position]
        holder.title.text = service.serviceType
        holder.description.text = service.description
        holder.name.text = "${service.mechanicName}"
        holder.price.text = "$${service.price.toInt()}"
        holder.rating.text = service.rating.toString()

        holder.selectButton.setOnClickListener {
            val intent = Intent(context, PostServiceRequestActivity::class.java)
            intent.putExtra(EXTRA_SERVICE, service)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size


}