package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.shared.messaging_test.MyMessage

class MessageAdapter(var context: Activity, var messages: ArrayList<MyMessage>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val inflator = LayoutInflater.from(context)
        return when (viewType) {
            MY_MESSAGE -> ViewHolder(inflator.inflate(R.layout.message_bubble_primary, null))
            else -> ViewHolder(inflator.inflate(R.layout.message_bubble_secondary, null))
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message = itemView.findViewById<TextView>(R.id.message_body)
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        val message = messages[position]
        holder.message.text = message.textBody
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return position % 2 * 2
    }
}

