package com.example.mobilemechanic.shared.messaging

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.messaging.Message
import com.example.mobilemechanic.model.adapter.MessageListAdapter
import com.example.mobilemechanic.model.messaging.ChatRoom
import com.example.mobilemechanic.model.messaging.ChatUserInfo
import com.example.mobilemechanic.model.messaging.EXTRA_CHAT_ROOM
import com.example.mobilemechanic.shared.utility.DateTimeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity()
{

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var messageListAdapter: MessageListAdapter
    private var messages = ArrayList<Message>()

    private lateinit var userType: UserType
    private lateinit var chatRoom: ChatRoom
    private lateinit var myInfo: ChatUserInfo
    private lateinit var theirInfo: ChatUserInfo

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
        chatRoom = intent.getParcelableExtra(EXTRA_CHAT_ROOM)

        when(userType)
        {
            UserType.CLIENT -> {
                myInfo = chatRoom.clientInfo
                theirInfo = chatRoom.mechanicInfo
            }
            UserType.MECHANIC -> {
                myInfo = chatRoom.mechanicInfo
                theirInfo = chatRoom.clientInfo
            }
        }

        if(myInfo.isNewcomer)
            sendGreetingMessage()
        else
            setUpMessagesRecyclerView()

        id_send_msg_btn.setOnClickListener {
            sendMessage(id_chat_log_field.text.toString())
            id_chat_log_field.setText("")
        }
        setUpActionBar()
    }

    private fun setUpMessagesRecyclerView(){
        viewManager = LinearLayoutManager(this)
        messageListAdapter = MessageListAdapter(this, messages)
        id_messages_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager.apply {
                reverseLayout = false
            }
            adapter = messageListAdapter
        }
        reactiveMessagesRecyclerView()
    }

    private fun reactiveMessagesRecyclerView() {
        chatRoomsRef.document(chatRoom.objectID).collection("Messages")
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
                viewManager.scrollToPosition(messageListAdapter.itemCount - 1)
            }
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_messages_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = myInfo.firstName
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun sendMessage(contents: String)
    {
        val message = Message(myInfo, contents, DateTimeManager.currentTimeMillis())
        chatRoomsRef.document(chatRoom.objectID).collection("Messages")
            .document(DateTimeManager.currentTimeMillis().toString())
            .set(message)
    }

    private fun sendGreetingMessage()
    {
        val myInfoField = when(userType){
            UserType.CLIENT -> {"clientInfo"}
            UserType.MECHANIC -> {"mechanicInfo"}
        }

        chatRoomsRef.document(chatRoom.objectID).collection("Messages")
            .whereEqualTo("$myInfoField.uid", mAuth.currentUser?.uid)
            .get().addOnSuccessListener {
                if(it.isEmpty)
                {
                    myInfo.isNewcomer = false
                    val contents = "${myInfo.firstName} joined the chat."
                    val greetingMessage =
                        Message(myInfo, contents, DateTimeManager.currentTimeMillis())
                    chatRoomsRef.document(chatRoom.objectID).collection("Messages")
                        .document(DateTimeManager.currentTimeMillis().toString())
                        .set(greetingMessage)
                        .addOnSuccessListener {
                            setUpMessagesRecyclerView()
                            val myInfoField = when(userType){
                                UserType.CLIENT -> {"clientInfo"}
                                UserType.MECHANIC -> {"mechanicInfo"}
                            }
                            chatRoomsRef.document(chatRoom.objectID).update(myInfoField, myInfo)
                        }
                }
                else
                {
                    setUpMessagesRecyclerView()
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

