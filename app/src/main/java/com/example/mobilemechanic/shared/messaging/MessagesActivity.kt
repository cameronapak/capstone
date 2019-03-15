package com.example.mobilemechanic.shared.messaging

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.EXTRA_USER_TYPE
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.model.messaging.Message
import com.example.mobilemechanic.model.adapter.MessageListAdapter
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity() {

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var messageListAdapter: MessageListAdapter
    private var messages = ArrayList<Message>()
    private lateinit var userType: UserType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        userType = UserType.valueOf(intent.getStringExtra(EXTRA_USER_TYPE))
        setUpMessagesRecyclerView()
    }

    private fun setUpMessagesRecyclerView(){
        viewManager = LinearLayoutManager(this)
        messageListAdapter = MessageListAdapter(this, messages)
        id_messages_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = messageListAdapter
        }
        mockData()
        reactiveServiceRecyclerView()
    }

    private fun reactiveServiceRecyclerView() {

    }

    private fun mockData(){
        for(i in 0..10){
            messages.add(Message())
        }
    }
}

