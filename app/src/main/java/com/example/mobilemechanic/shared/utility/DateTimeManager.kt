package com.example.mobilemechanic.shared.utility

import java.text.SimpleDateFormat
import java.util.*

object DateTimeManager {
    fun millisToDate(milliSeconds: Long?, dateFormat: String): String {
        if (milliSeconds == null) {
            return ""
        }
        val formatter = SimpleDateFormat(dateFormat)
        return formatter.format(Date(milliSeconds))
    }

    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}