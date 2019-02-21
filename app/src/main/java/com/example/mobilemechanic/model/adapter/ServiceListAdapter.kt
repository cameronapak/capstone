package com.example.mobilemechanic.model.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Service

class ServiceListAdapter(var context: Context, var serviceArray: ArrayList<Service>) :
    RecyclerView.Adapter<ServiceListAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListAdapter.ViewHolder
    {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_service_manage, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return serviceArray.size
    }

    override fun onBindViewHolder(holder: ServiceListAdapter.ViewHolder, position: Int)
    {
        holder.bindItem(position)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
    {
        fun bindItem(position: Int)
        {
            val serviceItem = serviceArray[position]
            val serviceType = itemView.findViewById<TextView>(R.id.id_service_type)
            val price = itemView.findViewById<TextView>(R.id.id_price)
            val description = itemView.findViewById<TextView>(R.id.id_description)
            val updateBtn = itemView.findViewById<Button>(R.id.id_button_update)
            val removeBtn = itemView.findViewById<Button>(R.id.id_button_remove)

            serviceType.text = serviceItem.serviceType
            price.text = serviceItem.price.toString()
            description.text = serviceItem.comment


            //itemView represents 1 card entry
//            removeBtn.setOnClickListener {
//                //Launch Edit Activity
//                val i = Intent(context, EditTraineeExercise::class.java)
//                i.putExtra(EXTRA_EXERCISE, traineeExercises[position])
//
//                //must cast to Activity to use startActivityForResult outside of activity classes
//                if(context is Activity)
//                    context.startActivityForResult(i, REQ_CODE_EDIT_EX)
//            }
        }
    }
}