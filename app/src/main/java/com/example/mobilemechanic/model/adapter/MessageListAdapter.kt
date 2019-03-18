package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.messaging.Message
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MessageListAdapter(var context: Activity, var messages: ArrayList<Message>) :
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

    private fun displayProfileImage(drawerProfileImage: CircleImageView, photoUrl: String) {
        val userProfileUri = Uri.parse(photoUrl)
        if (userProfileUri != null) {
            Picasso.get().load(userProfileUri).into(drawerProfileImage)
        } else {
            Picasso.get().load(R.drawable.ic_circle_profile).into(drawerProfileImage)
        }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val profilePhoto = itemView.findViewById<CircleImageView>(R.id.id_message_profile_image)
        val textMessage = itemView.findViewById<TextView>(R.id.id_text_message)
        val timeStamp = itemView.findViewById<TextView>(R.id.id_text_timestamp)
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        val message = messages[position]
        holder.textMessage.text = message.contents
        holder.timeStamp.text = DateTimeManager.millisToDate(message.timeStamp, "h:mm a")
        val photoUrl = message.chatUserInfo.photoUrl
        displayProfileImage(holder.profilePhoto ,photoUrl)
    }
}

