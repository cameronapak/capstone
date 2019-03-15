package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.messaging.Message
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class MessageListAdapter(var context: Activity, var messages: ArrayList<Message>, var userType: UserType) :
    RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var chatRoomsRef: CollectionReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_messages_card, parent, false)
        mFirestore = FirebaseFirestore.getInstance()
        chatRoomsRef = mFirestore?.collection(context.getString(R.string.ref_chatRooms))
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        //setup cardview stuff here
        val name = itemView.findViewById<TextView>(R.id.id_name)
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        val message = messages[position]
        when(userType)
        {
            UserType.CLIENT -> {
                setUpClientDisplay(holder, message)
            }
            UserType.MECHANIC -> {
                setUpMechanicDisplay(holder, message)
            }
        }
    }

    private fun setUpClientDisplay(holder: MessageListAdapter.ViewHolder, message: Message)
    {

    }

    private fun setUpMechanicDisplay(holder: MessageListAdapter.ViewHolder, message: Message)
    {

    }
}

