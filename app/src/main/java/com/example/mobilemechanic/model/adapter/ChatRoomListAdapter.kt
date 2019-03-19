package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.messaging.ChatRoom
import com.example.mobilemechanic.model.messaging.EXTRA_CHAT_ROOM
import com.example.mobilemechanic.model.messaging.Message
import com.example.mobilemechanic.shared.USER_TAG
import com.example.mobilemechanic.shared.messaging.MessagesActivity
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ChatRoomListAdapter(var context: Activity, var chatRooms: ArrayList<ChatRoom>, var userType: UserType) :
    RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder>()
{

    private var mFirestore = FirebaseFirestore.getInstance()
    private lateinit var messagesRef: CollectionReference

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val profileImage = itemView.findViewById<CircleImageView>(R.id.id_profile_image)
        val otherMemberName = itemView.findViewById<TextView>(R.id.id_other_member_name)
        val latestMessage = itemView.findViewById<TextView>(R.id.id_latest_message)
        val latestMessageTimeStamp = itemView.findViewById<TextView>(R.id.id_latest_message_time_stamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_chat_room, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    override fun onBindViewHolder(holder: ChatRoomListAdapter.ViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        when(userType)
        {
            UserType.CLIENT -> {
                setUpClientDisplay(holder, chatRoom)
            }
            UserType.MECHANIC -> {
                setUpMechanicDisplay(holder, chatRoom)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MessagesActivity::class.java)
            Log.d(USER_TAG, "[ChatRoomListAdapter] userType: ${userType.name}")
            Log.d(USER_TAG, "[ChatRoomListAdapter] chatRoom: $chatRoom")

            intent.putExtra(EXTRA_USER_TYPE, userType.name)
            intent.putExtra(EXTRA_CHAT_ROOM, chatRoom)
            context.startActivity(intent)
        }

        getChatRoomLatestMessage(chatRoom, holder)
    }

    private fun getChatRoomLatestMessage(chatRoom: ChatRoom, holder: ChatRoomListAdapter.ViewHolder) {
        val otherUserUid = when(userType){
            UserType.CLIENT -> chatRoom.mechanicMember.uid
            UserType.MECHANIC -> chatRoom.clientMember.uid
        }

        var latestMessage: Message
        messagesRef = mFirestore.collection("ChatRooms/${chatRoom.objectID}/Messages")
        messagesRef.whereEqualTo("chatUserInfo.uid", otherUserUid)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Log.d(USER_TAG, "[ChatRoomListAdapter] ${exception.message}")
                return@addSnapshotListener
            }

            if (querySnapshot != null && !querySnapshot.isEmpty) {
                latestMessage = querySnapshot.single().toObject(Message::class.java)
                Log.d(USER_TAG, "[ChatRoomListAdapter] chatroom ${chatRoom.objectID} latest message $latestMessage")
                holder.latestMessage.text = latestMessage.contents
                holder.latestMessageTimeStamp.text = DateTimeManager.millisToDate(latestMessage.timeStamp, "h:m a")
            }
        }
    }

    private fun setUpClientDisplay(holder: ChatRoomListAdapter.ViewHolder, chatRoom: ChatRoom)
    {
        if (chatRoom.mechanicMember.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.ic_circle_profile).into(holder.profileImage)
        } else {
            Picasso.get().load(Uri.parse(chatRoom.mechanicMember.photoUrl)).into(holder.profileImage)
        }

        holder.otherMemberName.text = "${chatRoom.mechanicMember.firstName} ${chatRoom.mechanicMember.lastName}"
    }

    private fun setUpMechanicDisplay(holder: ChatRoomListAdapter.ViewHolder, chatRoom: ChatRoom)
    {
        if (chatRoom.mechanicMember.photoUrl.isNullOrEmpty()) {
            Picasso.get().load(R.drawable.ic_circle_profile).into(holder.profileImage)
        } else {
            Picasso.get().load(Uri.parse(chatRoom.clientMember.photoUrl)).into(holder.profileImage)
        }

        holder.otherMemberName.text = "${chatRoom.clientMember.firstName} ${chatRoom.clientMember.lastName}"
    }
}