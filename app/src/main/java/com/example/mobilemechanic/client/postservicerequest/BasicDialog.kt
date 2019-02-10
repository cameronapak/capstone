package com.example.mobilemechanic.client.postservicerequest

import android.app.Activity
import android.app.Dialog
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.basic_dialog.*


class BasicDialog(activity: Activity, val container: View, private val bodyContent: View)  {

    private var dialog: Dialog = Dialog(activity)

    fun show() {
//        val dialog = Dialog(activity)
        dialog.apply {
            setContentView(container)
        }

        val negative = dialog.id_negative as TextView
        val body = dialog.id_main_content as ConstraintLayout
        body.addView(bodyContent)
        negative.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}