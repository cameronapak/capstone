package com.example.mobilemechanic.shared.messaging

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.adapter.MessageListAdapter
import com.example.mobilemechanic.model.messaging.ChatRoom
import com.example.mobilemechanic.model.messaging.EXTRA_CHAT_ROOM
import com.example.mobilemechanic.model.messaging.Message
import com.example.mobilemechanic.shared.USER_TAG
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
    private lateinit var memberType: String

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var chatRoomsRef: CollectionReference
    private lateinit var messagesRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        userType = UserType.valueOf(intent.getStringExtra(EXTRA_USER_TYPE))
        chatRoom = intent.getParcelableExtra(EXTRA_CHAT_ROOM)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        chatRoomsRef = mFirestore.collection(getString(R.string.ref_chatRooms))
        messagesRef = chatRoomsRef.document(chatRoom.objectID).collection("Messages")

        Log.d(USER_TAG, "[MessagesActivity] userType: $userType")
        Log.d(USER_TAG, "[MessagesActivity] chatRoom: $chatRoom")

        setUpMessagesActivity()
    }

    private fun setUpMessagesActivity() {
        getMemberType()
        setUpActionBar()
        setUpSendMessage()
        setUpMessagesRecyclerView()
        joinChatRoom()
    }

    private fun getMemberType() {
        memberType = when(userType){
            UserType.CLIENT -> "clientMember"
            UserType.MECHANIC -> "mechanicMember"
        }
    }

    private fun setUpActionBar() {
        val otherMember = chatRoom.getOtherMember(mAuth.currentUser?.uid)
        id_other_member_name.text = "${otherMember.firstName} ${otherMember.lastName}"

        setSupportActionBar(id_messages_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpSendMessage() {
        id_send_message.setOnClickListener {
            sendMessage(id_message_input.text.toString())
        }
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

    private fun joinChatRoom() {
        val member = chatRoom.getMember(mAuth.currentUser?.uid)
        messagesRef.whereEqualTo("$memberType.uid", mAuth.currentUser?.uid)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty) {

                    val contents = "${member?.firstName} joined the chat."
                    val currentTime = DateTimeManager.currentTimeMillis()
                    val greetingMessage = Message(member, contents, currentTime)

                    messagesRef.document(currentTime.toString()).set(greetingMessage)
                        .addOnSuccessListener {
                            Log.d(USER_TAG, "[MessagesActivity] joined chat greeting send successfully")
                        }.addOnFailureListener {
                            Log.d(USER_TAG, "[MessagesActivity] ${it.message}")
                        }
                }
            }
    }

    private fun sendMessage(contents: String)
    {
        val member = chatRoom.getMember(mAuth.currentUser?.uid)
        val message = Message(member, contents, DateTimeManager.currentTimeMillis())
        messagesRef.document(DateTimeManager.currentTimeMillis().toString())
            .set(message).addOnCompleteListener {
                id_message_input.text.clear()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

