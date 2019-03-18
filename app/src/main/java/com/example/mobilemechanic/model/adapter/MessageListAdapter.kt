package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.messaging.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import de.hdodenhof.circleimageview.CircleImageView

const val MY_MESSAGE = 1
const val YOUR_MESSAGE = 2
class MessageListAdapter(var context: Activity, var messages: ArrayList<Message>) :
    RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var chatRoomsRef: CollectionReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            MY_MESSAGE -> ViewHolder(inflater.inflate(R.layout.message_bubble_primary, null))
            else -> ViewHolder(inflater.inflate(R.layout.message_bubble_secondary, null))
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val profilePhoto = itemView.findViewById<CircleImageView>(R.id.id_message_profile_image)
        val textMessage = itemView.findViewById<TextView>(R.id.id_message_body)
//        val timeStamp = itemView.findViewById<TextView>(R.id.id_text_timestamp)
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        val message = messages[position]
        holder.textMessage.text = message.contents
//        holder.timeStamp.text = DateTimeManager.millisToDate(message.timeStamp, "h:mm a")
    }

    override fun getItemViewType(position: Int): Int {
        val chatUserInfo = messages[position].chatUserInfo
        return if (mAuth?.currentUser?.uid == chatUserInfo.uid)
            MY_MESSAGE
        else
            YOUR_MESSAGE
    }
}

