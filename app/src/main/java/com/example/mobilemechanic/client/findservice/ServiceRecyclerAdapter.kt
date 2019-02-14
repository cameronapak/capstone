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
import com.example.mobilemechanic.model.User

const val EXTRA_MECHANIC = "extra_echanic"

class ServiceRecyclerAdapter(val context: Context, val dataset: ArrayList<User>) :
    RecyclerView.Adapter<ServiceRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.id_title)
        val description = itemView.findViewById<TextView>(R.id.id_description)
        val name = itemView.findViewById<TextView>(R.id.id_name)
        val selectButton = itemView.findViewById<Button>(R.id.id_select_button)
        val price = itemView.findViewById<TextView>(R.id.id_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceRecyclerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_service, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = dataset[position]
        holder.title.text = "Mechanic"
        holder.name.text = "${user.firstName} ${user.lastName}"
        holder.price.text = "$30"

        holder.selectButton.setOnClickListener {

            val intent = Intent(context, PostServiceRequestActivity::class.java)
            intent.putExtra(EXTRA_MECHANIC, user)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size


}