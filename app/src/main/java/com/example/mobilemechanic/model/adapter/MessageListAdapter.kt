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
import com.example.mobilemechanic.shared.utility.ScreenManager
import com.example.mobilemechanic.shared.utility.StringManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.squareup.picasso.Picasso
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
        val profilePhoto = itemView.findViewById<CircleImageView>(R.id.id_profile_image)
        val textMessage = itemView.findViewById<TextView>(R.id.id_message_body)
        val otherMemberName = itemView.findViewById<TextView>(R.id.id_name)
        val timeStamp = itemView.findViewById<TextView>(R.id.id_time_stamp)
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        val message = messages[position]
        holder.textMessage.text = message.contents

        if (holder.itemViewType == YOUR_MESSAGE) {
            holder.otherMemberName.text = StringManager.firstLetterToUppercase(message.chatUserInfo.firstName)
            holder.timeStamp.text = DateTimeManager.millisToDate(message.timeStamp, "h:m a")
        }

        displayProfileImage(holder.profilePhoto , message.chatUserInfo.photoUrl)
        hideKeyBoardOnItemClick(holder)
    }

    private fun hideKeyBoardOnItemClick(holder: MessageListAdapter.ViewHolder) {
        holder.itemView.setOnClickListener {
            ScreenManager.hideKeyBoard(context)
        }
    }

    private fun displayProfileImage(drawerProfileImage: CircleImageView, photoUrl: String) {
        val userProfileUri = Uri.parse(photoUrl)
        if (userProfileUri != null) {
            Picasso.get().load(userProfileUri).into(drawerProfileImage)
        } else {
            Picasso.get().load(R.drawable.ic_circle_profile).into(drawerProfileImage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatUserInfo = messages[position].chatUserInfo
        return if (mAuth?.currentUser?.uid == chatUserInfo.uid) {
            MY_MESSAGE
        } else {
            YOUR_MESSAGE
        }
    }
}

