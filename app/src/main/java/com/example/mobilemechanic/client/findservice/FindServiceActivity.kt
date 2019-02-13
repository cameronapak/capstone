package com.example.mobilemechanic.client.findservice

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import com.example.mobilemechanic.model.Service
import com.example.mobilemechanic.model.ServiceType
import com.example.mobilemechanic.model.User
import com.example.mobilemechanic.model.UserType
import com.example.mobilemechanic.shared.ScreenManager
import kotlinx.android.synthetic.main.activity_find_service.*

class FindServiceActivity : AppCompatActivity() {

    private lateinit var mechanics: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_service)

        mechanics = ArrayList<User>()

        val viewManager = LinearLayoutManager(this)

        //Mock data
        val user = User(
            "",
            "fake@gmail.com",
            "password",
            UserType.MECHANIC,
            "Jason",
            "Statham",
            "405",
            "123 street",
            "Boston",
            "MA",
            "02125",
            ""
        )

        val service = Service(ServiceType.OIL_CHANGE, 30.00, "")
        user.services?.add(service)

        mechanics.add(user)

        val serviceAdapter = ServiceRecyclerAdapter(this, mechanics)
        id_recyclerview_services.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = serviceAdapter
        }

        serviceAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        ScreenManager.hideStatusAndBottomNavigationBar(this)
    }
}
