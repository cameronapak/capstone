package com.example.mobilemechanic.shared.messaging

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.messaging.Message
import com.example.mobilemechanic.model.adapter.MessageListAdapter
import com.example.mobilemechanic.model.messaging.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity() {

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var messageListAdapter: MessageListAdapter
    private var messages = ArrayList<Message>()
    private lateinit var userType: UserType
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var chatRoomsRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        chatRoomsRef = mFirestore.collection(getString(R.string.ref_chatRooms))
        userType = UserType.valueOf(intent.getStringExtra(EXTRA_USER_TYPE))
        setUpMessagesRecyclerView()
    }

    private fun setUpMessagesRecyclerView(){
        viewManager = LinearLayoutManager(this)
        messageListAdapter = MessageListAdapter(this, messages, userType)
        id_messages_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = messageListAdapter
        }
        mockData()
        //reactiveServiceRecyclerView()
    }

    private fun reactiveServiceRecyclerView() {
        chatRoomsRef.whereEqualTo("mechanicInfo.uid", mAuth?.currentUser?.uid.toString())
            ?.addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                messages.clear()
                for (doc in querySnapshot!!) {
                    val message = doc.toObject(Message::class.java)
                    messages.add(message)
                }
                messageListAdapter.notifyDataSetChanged()
            }
    }

    private fun mockData(){
        for(i in 0..10){
            messages.add(Message())
        }
    }
}

