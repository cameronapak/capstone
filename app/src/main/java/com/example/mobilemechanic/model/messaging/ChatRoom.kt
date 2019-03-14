package com.example.mobilemechanic.model.messaging

data class ChatRoom(var clientInfo: ChatUserInfo = ChatUserInfo(), var mechanicInfo: ChatUserInfo = ChatUserInfo())
{
}