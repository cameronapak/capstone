package com.example.mobilemechanic.shared.messaging

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.adapter.ChatRoomListAdapter
import com.example.mobilemechanic.model.messaging.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat_rooms.*

class ChatRoomsActivity : AppCompatActivity()
{
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var chatRoomsRef: CollectionReference

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var chatRoomListAdapter: ChatRoomListAdapter
    private var chatRooms = ArrayList<ChatRoom>()
    private lateinit var userType: UserType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_rooms)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        chatRoomsRef = mFirestore.collection(getString(R.string.ref_chatRooms))
        userType = UserType.valueOf(intent.getStringExtra(EXTRA_USER_TYPE))
        setUpChatRoomRecyclerView()
    }

    private fun setUpChatRoomRecyclerView(){
        viewManager = LinearLayoutManager(this)
        chatRoomListAdapter = ChatRoomListAdapter(this, chatRooms, userType)
        id_chat_room_recylerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = chatRoomListAdapter
        }
        reactiveChatRoomRecyclerView()
    }

    private fun reactiveChatRoomRecyclerView() {
        chatRoomsRef.whereEqualTo("mechanicInfo.uid", mAuth?.currentUser?.uid.toString())
            ?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                chatRooms.clear()
                for (doc in querySnapshot!!) {
                    val chatRoom = doc.toObject(ChatRoom::class.java)
                    chatRoom.objectID = doc.id
                    chatRooms.add(chatRoom)
                }
                chatRoomListAdapter.notifyDataSetChanged()
            }
    }
}
