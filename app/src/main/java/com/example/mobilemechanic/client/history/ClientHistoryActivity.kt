package com.example.mobilemechanic.client.history

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.DataProviderManager
import com.example.mobilemechanic.model.Request
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.Vehicle
import com.example.mobilemechanic.shared.utility.ScreenManager
import kotlinx.android.synthetic.main.activity_client_history.*


class ClientHistoryActivity : AppCompatActivity() {

    private lateinit var request: ArrayList<Request>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_history)

        request = ArrayList()

        val viewManager = LinearLayoutManager(this)

        //Mock data
        val vehicle = Vehicle(
            "",
            "2011",
            "Toyota",
            "Venza",
            ""
        )

        val service = Service(
            "${DataProviderManager.getAllServices()[1]}",
            "",
            30.0
        )

//        val r = Request(
//            "objectID",
//            "ClientId",     //Temporary used for Name
//            "MechanicId",    //Temporary used for Name
//            "description",
//            vehicle.toString(),
//            service,
//            Status.Complete,
//            0L,
//            0L
//        )
//
//        request.add(r)
//        request.add(r)
//        request.add(r)
//        request.add(r)
//        request.add(r)
//        request.add(r)

        val historyAdapter = ClientHistoryRecyclerAdapter(this, request)
        id_recyclerview_history.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = historyAdapter
        }

        historyAdapter.notifyDataSetChanged()
        setUpToolBar()
    }

    private fun setUpToolBar() {
        setSupportActionBar(id_client_history_toolbar as Toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            title = "History"
            subtitle = "Previous services"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)

    }
}
