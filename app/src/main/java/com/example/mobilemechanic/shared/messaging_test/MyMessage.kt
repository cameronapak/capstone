package com.example.mobilemechanic.shared.messaging_test

data class MyMessage(var textBody: String, var memberData: MemberData, var isBelongsToCurrentUser: Boolean) {
}