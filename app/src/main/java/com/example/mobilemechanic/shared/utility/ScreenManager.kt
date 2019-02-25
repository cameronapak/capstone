package com.example.mobilemechanic.shared.utility

import android.app.Activity
import android.app.Dialog
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

object ScreenManager {
    fun hideStatusAndBottomNavigationBar(activity: Activity) {
        hideStatusBar(activity)
        hideBottomNavigationBar(activity)
    }

    private fun hideStatusBar(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun hideBottomNavigationBar(activity: Activity) {
        activity.window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    fun setBasicDialogWidth(activity: Activity, dialog: Dialog, proportion: Float)
            : WindowManager.LayoutParams {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window.attributes)
        val dialogWindowWidth = (displayWidth * proportion).toInt()
        layoutParams.width = dialogWindowWidth
        return layoutParams
    }
}