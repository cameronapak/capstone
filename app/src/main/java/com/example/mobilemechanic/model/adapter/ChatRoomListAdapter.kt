package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.content.Intent
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
import com.example.mobilemechanic.shared.USER_TAG
import com.example.mobilemechanic.shared.messaging.MessagesActivity

class ChatRoomListAdapter(var context: Activity, var chatRooms: ArrayList<ChatRoom>, var userType: UserType) :
    RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder>()
{

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val otherMemberName = itemView.findViewById<TextView>(R.id.id_other_member_name)
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
    }

    private fun setUpClientDisplay(holder: ChatRoomListAdapter.ViewHolder, chatRoom: ChatRoom)
    {
        holder.otherMemberName.text = "${chatRoom.mechanicMember.firstName} ${chatRoom.mechanicMember.lastName}"
    }

    private fun setUpMechanicDisplay(holder: ChatRoomListAdapter.ViewHolder, chatRoom: ChatRoom)
    {
        holder.otherMemberName.text = "${chatRoom.clientMember.firstName} ${chatRoom.clientMember.lastName}"
    }
}