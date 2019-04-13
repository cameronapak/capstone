package com.example.mobilemechanic.shared

import android.app.Activity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.mobilemechanic.R

enum class ToastyType {
    SUCCESS,
    FAIL,
    WARNING
}

object Toasty {
    fun makeText(context: Activity, message: String, type: ToastyType) {
        val inflater = context.layoutInflater
        var layout: View = when(type) {
            ToastyType.SUCCESS ->
                inflater.inflate(R.layout.toast_success, context.findViewById(R.id.custom_toast_container))
            ToastyType.FAIL ->
                inflater.inflate(R.layout.toast_fail, context.findViewById(R.id.custom_toast_container))
            ToastyType.WARNING ->
                inflater.inflate(R.layout.toast_warning, context.findViewById(R.id.custom_toast_container))
        }


        val text: TextView = layout.findViewById(R.id.text)
        text.text = message

        with (Toast(context.applicationContext)) {
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }
}
