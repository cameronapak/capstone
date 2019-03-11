package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.R
import com.example.mobilemechanic.mechanic.MECHANIC_TAG
import com.example.mobilemechanic.model.Request
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class MessageListAdapter(var context: Activity, var msgArray: ArrayList<Request>) :
    RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var serviceRef: CollectionReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_mesg_welcome, parent, false)
        mFirestore = FirebaseFirestore.getInstance()
        serviceRef = mFirestore?.collection("Services")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return msgArray.size
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        holder.bindItem(position)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bindItem(position: Int) {
            val serviceItem = msgArray[position]
            Log.d(MECHANIC_TAG, "[ServiceListAdapter] objectID: ${serviceItem.objectID}")
            val name = itemView.findViewById<TextView>(R.id.id_client_name)
            val description = itemView.findViewById<TextView>(R.id.id_description)

        }
    }

}

