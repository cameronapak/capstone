package com.example.mobilemechanic.client.garage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.mobilemechanic.R
import kotlinx.android.synthetic.main.activity_view_vehicle_info.*
import kotlinx.android.synthetic.main.card_view.*

class ViewVehicleInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_vehicle_info)

        id_recycler_view.layoutManager= LinearLayoutManager(this)
        var a = Intent()
        var message = a.getStringExtra("data")
        id_vehicle_info.setText(message)








    }
}
