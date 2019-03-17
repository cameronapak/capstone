package com.example.mobilemechanic.shared.messaging_test

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.mobilemechanic.model.adapter.MessageAdapter
import com.example.mobilemechanic.shared.USER_TAG
import kotlinx.android.synthetic.main.activity_chat_room.*
import java.util.*


class ChatRoomActivity : AppCompatActivity() {
    private var roomName = "observable-myroom"
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var garageRecyclerAdapter: MessageAdapter
    private var messages = ArrayList<MyMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.mobilemechanic.R.layout.activity_chat_room)
        setUpChatRoomActivity()

        id_send_message.setOnClickListener {
            val message = id_message_input.text.toString()
            Log.d(USER_TAG, "[ChatRoomActivity] send message: $message")
            id_message_input.text.clear()
        }

    }

    private fun setUpChatRoomActivity() {
        setUpActionBar()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        messages.add(MyMessage("Hey", MemberData("", ""), false))
        messages.add(MyMessage("Hey", MemberData("", ""), false))
        messages.add(
            MyMessage(
                "Hey this is a really long text what do you think if you just make it really really long. How about we go out",
                MemberData("", ""),
                false
            )
        )

        viewManager = LinearLayoutManager(this)
        garageRecyclerAdapter = MessageAdapter(this, messages)
        id_messages_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = garageRecyclerAdapter
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(id_chatroom_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
