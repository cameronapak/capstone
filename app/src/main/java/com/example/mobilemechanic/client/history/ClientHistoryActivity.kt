package com.example.mobilemechanic.client.history

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.*
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.client_activity_history.*


class ClientHistoryActivity : AppCompatActivity() {

    private lateinit var request: ArrayList<Request>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.client_activity_history)

        request = ArrayList<Request>()

        val viewManager = LinearLayoutManager(this)

        //Mock data
        val vehicle = Vehicle(
            2011,
            "Toyota",
            "Venza",
            ""
        )

        val service = Service(
            ServiceType.OIL_CHANGE,
            30.0,
            ""
        )

        val r = Request(
            " Client",     //Temporary used for Name
            "Jason",    //Temporary used for Name
            "",
            vehicle,
            service,
            true,
            0L,
            0L
        )

        request.add(r)
        request.add(r)
        request.add(r)
        request.add(r)
        request.add(r)
        request.add(r)

        val historyAdapter = ClientHistoryRecyclerAdapter(this, request)
        id_recyclerview_history.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = historyAdapter
        }

        historyAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)

    }
}
