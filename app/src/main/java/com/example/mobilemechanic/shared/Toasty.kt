package com.example.mobilemechanic.shared

import android.app.Activity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.toasty_basic.view.*

class Toasty(parentActivity: Activity, container: View, message: String): Toast(parentActivity) {
    class builder{
        companion object {
            var message = "Toast Message"

            fun build(parentActivity: Activity,container: View, message: String) : Toast{
                var toast = Toasty(parentActivity, container, message)

                val body = container.id_basic_toast
                container.apply {
                    id_basic_toast.text = message
                }
                return toast
            }
        }
    }
}
