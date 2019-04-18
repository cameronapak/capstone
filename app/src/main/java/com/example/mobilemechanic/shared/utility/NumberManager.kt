package com.example.mobilemechanic.shared.utility

import java.math.BigDecimal

object NumberManager {
    fun round(number: Float, decimalPlace: Int): Float {
        val bigDecimal = BigDecimal(number.toString())
        bigDecimal.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
        return bigDecimal.toFloat()
    }

    fun isNumeric(num: String): Boolean {
        try {
            val d = java.lang.Double.parseDouble(num)
        } catch (nfe: NumberFormatException) {
            return false
        } catch (nfe: NullPointerException) {
            return false
        }
        return true
    }
}