package com.example.mobilemechanic.client.findservice

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.mobilemechanic.R
import com.example.mobilemechanic.client.postservicerequest.PostServiceRequestActivity
import kotlinx.android.synthetic.main.activity_find_service.*

class FindServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_service)


        id_post_request.setOnClickListener {
            startActivity(Intent(this, PostServiceRequestActivity::class.java))
        }
    }
}
