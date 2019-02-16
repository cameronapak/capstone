package com.example.mobilemechanic.shared

import android.app.Dialog
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.View
import kotlinx.android.synthetic.main.basic_dialog.view.*


class BasicDialog {

    class Builder {
        companion object {
            var title = "Title"
            var positive = "Yes"
            var negative = "No"

            fun build(context: Context, container: View, bodyView: View): Dialog {
                var dialog = Dialog(context)
                dialog.setContentView(container)
                val body = container.id_body as ConstraintLayout
                container.apply {
                    id_service_type.text = title
                    id_positive.text = positive
                    id_negative.text = negative
                }
                body.addView(bodyView)
                return dialog
            }
        }
    }
}