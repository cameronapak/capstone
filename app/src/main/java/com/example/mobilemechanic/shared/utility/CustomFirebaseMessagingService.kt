package com.example.mobilemechanic.shared.utility

import android.util.Log
import com.example.mobilemechanic.client.CLIENT_TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        var notificationTitle = ""
        var notificationBody = ""
        var notificationData = ""

        try {
            notificationTitle = remoteMessage.notification?.title.toString()
            notificationBody = remoteMessage.notification?.body.toString()
            notificationData = remoteMessage.data.toString()
        } catch (e: NullPointerException) {

        }

        Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] title: $notificationTitle")
        Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] body: $notificationBody")
        Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] data: $notificationData")
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}