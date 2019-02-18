package com.example.mobilemechanic.shared

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.LinearLayout
import com.example.mobilemechanic.shared.utility.ScreenManager
import kotlinx.android.synthetic.main.basic_dialog.view.*




class BasicDialog {

    class Builder {
        companion object {
            var title = "Title"
            var positive = "Yes"
            var negative = "No"

            fun build(parentActivity: Activity, container: View, bodyView: View): Dialog {
                var dialog = Dialog(parentActivity)
                dialog.setContentView(container)

                val body = container.id_body as LinearLayout
                container.apply {
                    id_status.text = title
                    id_positive.text = positive
                    id_negative.text = negative
                }
                body.addView(bodyView)
                dialog.window.attributes =
                    ScreenManager.setBasicDialogWidth(parentActivity, dialog, 0.9f)
                return dialog
            }
        }
    }
}