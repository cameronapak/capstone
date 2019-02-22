package com.example.mobilemechanic.model.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.MECHANIC_TAG
import com.example.mobilemechanic.model.algolia.ServiceModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ServiceListAdapter(var context: Context, var serviceArray: ArrayList<ServiceModel>) :
    RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var serviceRef: CollectionReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_service_manage, parent, false)
        mFirestore = FirebaseFirestore.getInstance()
        serviceRef = mFirestore?.collection("Services")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return serviceArray.size
    }

    override fun onBindViewHolder(holder: ServiceListAdapter.ViewHolder, position: Int) {
        holder.bindItem(position)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bindItem(position: Int) {
            val serviceItem = serviceArray[position]
            Log.d(MECHANIC_TAG, "[ServiceListAdapter] objectID: ${serviceItem.objectID}")
            val serviceType = itemView.findViewById<TextView>(R.id.id_service_type)
            val price = itemView.findViewById<TextView>(R.id.id_price)
            val description = itemView.findViewById<TextView>(R.id.id_description)
            val updateBtn = itemView.findViewById<Button>(R.id.id_select)
            val removeBtn = itemView.findViewById<Button>(R.id.id_button_remove)

            serviceType.text = serviceItem.serviceType
            price.text = serviceItem.price.toString()
            description.text = serviceItem.description


            removeBtn.setOnClickListener {
                Log.d(MECHANIC_TAG, "[ServiceListAdapter] remove ${serviceItem.objectID}")
                serviceRef.document("${serviceItem.objectID}").delete().addOnSuccessListener {
                    Toast.makeText(context, "Service Removed Successfully", Toast.LENGTH_LONG).show()
                }?.addOnFailureListener {
                    Toast.makeText(context, "Failed to remove service", Toast.LENGTH_LONG).show()
                    Log.w(MECHANIC_TAG, "Error deleting services", it)
                }
            }
        }
    }
}