package com.example.mobilemechanic.mechanic.messaging

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.messaging.Message
import com.example.mobilemechanic.model.adapter.MessageListAdapter
import kotlinx.android.synthetic.main.activity_mechanic_messages.*

class MechanicMessages : AppCompatActivity() {

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var messageListAdapter: MessageListAdapter
    private var messages = ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mechanic_messages)
        setUpMessagesRecyclerView()
    }

    private fun setUpMessagesRecyclerView(){
        viewManager = LinearLayoutManager(this)
        messageListAdapter = MessageListAdapter(this, messages)
        id_mechanic_messages_recyclerview.apply {
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

