package com.example.mobilemechanic.shared.messaging

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.adapter.ChatRoomListAdapter
import com.example.mobilemechanic.model.messaging.ChatRoom
import com.example.mobilemechanic.shared.signin.USER_TAG
import com.example.mobilemechanic.shared.utility.ScreenManager
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
    private lateinit var emptyView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_rooms)
        emptyView = findViewById(R.id.id_empty_state_view)
        userType = UserType.valueOf(intent.getStringExtra(EXTRA_USER_TYPE))

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        chatRoomsRef = mFirestore.collection(getString(R.string.ref_chatRooms))
        Log.d(USER_TAG, "[ChatRoomsActivity] userType: $userType")
        setUpChatRoomRecyclerView()
        setUpActionBar()
    }

    private fun setUpChatRoomRecyclerView(){
        viewManager = LinearLayoutManager(this)
        chatRoomListAdapter = ChatRoomListAdapter(this, chatRooms, userType)
        id_chat_room_recylerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = chatRoomListAdapter
            addItemDecoration(DividerItemDecoration(id_chat_room_recylerview.context, viewManager.orientation))
        }
        reactiveChatRoomRecyclerView()
    }

    private fun reactiveChatRoomRecyclerView() {
        val memberType = when(userType){
            UserType.CLIENT -> "clientMember"
            UserType.MECHANIC -> "mechanicMember"
        }

        Log.d(USER_TAG, "[ChatRoomsActivity] memberType.uid: $memberType.uid")
        Log.d(USER_TAG, "[ChatRoomsActivity] mAuth uid: ${mAuth.currentUser?.uid}")

        chatRoomsRef.whereEqualTo("$memberType.uid", mAuth.currentUser?.uid)
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

                // toggle empty state view
                if (chatRooms.isNullOrEmpty()) {
                    Log.d(
                        USER_TAG,
                        "[ChatRoomsActivity] Messages are empty! Showing empty state view."
                    )
                    emptyView.setVisibility(View.VISIBLE)
                } else {
                    emptyView.setVisibility(View.GONE)
                }

                chatRoomListAdapter.notifyDataSetChanged()
            }
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_chat_room_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "Messaging"
            subtitle = "Active messages"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
