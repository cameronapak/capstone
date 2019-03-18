package com.example.mobilemechanic.model.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.messaging.ChatRoom
import com.example.mobilemechanic.model.messaging.EXTRA_CHAT_ROOM
import com.example.mobilemechanic.shared.messaging.MessagesActivity
import com.google.android.gms.maps.model.Circle
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatRoomListAdapter(var context: Activity, var chatRooms: ArrayList<ChatRoom>, var userType: UserType) :
    RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder>()
{
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var chatRoomsRef: CollectionReference

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = itemView.findViewById<TextView>(R.id.id_client_name)
        val profilePhoto = itemView.findViewById<CircleImageView>(R.id.id_chat_profile_image)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomListAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recyclerview_item_chat_room, parent, false)
        mFirestore = FirebaseFirestore.getInstance()
        chatRoomsRef = mFirestore.collection(context.getString(R.string.ref_chatRooms))
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

        //open MessagesActivity by tapping on a chatroom in the recyclerview
        holder.itemView.setOnClickListener {
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra(EXTRA_USER_TYPE, userType.name)
            intent.putExtra(EXTRA_CHAT_ROOM, chatRoom)
            context.startActivity(intent)
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

    private fun setUpClientDisplay(holder: ChatRoomListAdapter.ViewHolder, chatRoom: ChatRoom)
    {
        holder.name.text = "${chatRoom.mechanicInfo.firstName} ${chatRoom.mechanicInfo.lastName}"
        val photoUrl = chatRoom.mechanicInfo.photoUrl
        displayProfileImage(holder.profilePhoto ,photoUrl)
    }

    private fun setUpMechanicDisplay(holder: ChatRoomListAdapter.ViewHolder, chatRoom: ChatRoom)
    {
        holder.name.text = "${chatRoom.clientInfo.firstName} ${chatRoom.clientInfo.lastName}"
        val photoUrl = chatRoom.clientInfo.photoUrl
        displayProfileImage(holder.profilePhoto ,photoUrl)
    }
}