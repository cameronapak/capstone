package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Message
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class MessageListAdapter(var context: Activity, var messages: ArrayList<Message>) :
    RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var serviceRef: CollectionReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_mesg_welcome, parent, false)
        mFirestore = FirebaseFirestore.getInstance()
        serviceRef = mFirestore?.collection("Messages")
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name = itemView.findViewById<TextView>(R.id.id_client_name)
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        val basicInfo = messages[position].theirInfo
        holder.name.text = basicInfo.firstName
    }

}

