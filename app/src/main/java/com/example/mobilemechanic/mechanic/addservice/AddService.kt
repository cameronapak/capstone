package com.example.mobilemechanic.mechanic.addservice

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import com.example.mobilemechanic.R


class AddService : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width*.8).toInt(), (height*.7).toInt())

        val param = window.attributes
        param.gravity = Gravity.CENTER
        param.x =0
        param.y = -20

        window.attributes = param
    }
}
