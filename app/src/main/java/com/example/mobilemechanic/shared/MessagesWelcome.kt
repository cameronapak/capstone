package com.example.mobilemechanic.shared

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.adapter.MessageListAdapter
import kotlinx.android.synthetic.main.activity_messages_welcome.*

class MessagesWelcome : AppCompatActivity() {

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var mechanicMsgListAdapter: MessageListAdapter
    private var requests = ArrayList<Request>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_welcome)
        setUpMsgRecyclerView()
    }

    private fun setUpMsgRecyclerView(){
        viewManager = LinearLayoutManager(this)
        mechanicMsgListAdapter = MessageListAdapter(this, requests)
        id_msg_welcome_recyl.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mechanicMsgListAdapter
        }
        reactiveServiceRecyclerView()
    }

    private fun reactiveServiceRecyclerView() {

    }
}

