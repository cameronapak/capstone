package com.example.mobilemechanic.shared

import android.app.Activity
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
}