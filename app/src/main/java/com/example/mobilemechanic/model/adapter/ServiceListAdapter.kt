package com.example.mobilemechanic.model.adapter

import android.app.Activity
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
            .inflate(R.layout.service_card_view, parent, false)
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
            //references to text views
            val typeView = itemView.findViewById<TextView>(R.id.text_service_type)
            val costView = itemView.findViewById<TextView>(R.id.label_cost_service)
            val descriptionView = itemView.findViewById<TextView>(R.id.label_service_description)
            val updateBtn = itemView.findViewById<Button>(R.id.id_button_choice)
            val removeBtn = itemView.findViewById<Button>(R.id.id_button_remove)



            typeView.text = serviceArray[position].serviceType
            if(context is Activity)
                costView.text = context.getString(R.string.price, serviceArray[position].price)
            descriptionView.text = serviceArray[position].comment


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