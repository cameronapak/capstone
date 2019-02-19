package com.example.mobilemechanic.shared

import android.app.Activity
import android.app.Dialog
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.basic_dialog.view.*




class BasicDialog {

    class Builder {
        companion object {
            var title = "Title"
            var positive = "Yes"
            var negative = "No"

            fun build(context: Activity, container: View, bodyView: View): Dialog {
                var dialog = Dialog(context)
                dialog.setContentView(container)
                val body = container.id_body as LinearLayout
                container.apply {
                    id_service_type.text = title
                    id_positive.text = positive
                    id_negative.text = negative
                }
                body.addView(bodyView)

                val displayMetrics = DisplayMetrics()
                context.windowManager.defaultDisplay.getMetrics(displayMetrics)
                val displayWidth = displayMetrics.widthPixels
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog.window.attributes)
                 val dialogWindowWidth = (displayWidth * 0.9f).toInt()
                layoutParams.width = dialogWindowWidth
                dialog.window.attributes = layoutParams
                return dialog
            }
        }
    }
}