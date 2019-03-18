package com.example.mobilemechanic.shared.utility

object StringManager {
    fun firstLetterToUppercase(text: String): String {
        return "${text.substring(0,1).toUpperCase()}${text.substring(1)}"
    }
}