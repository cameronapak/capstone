package com.example.mobilemechanic.shared.utility

import android.util.Log
import com.example.mobilemechanic.client.CLIENT_TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
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

    override fun onNewToken(token: String) {
        Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] onNewToken() token: $token")
        sendFcmTokenToFirestore(token)
    }

    private fun sendFcmTokenToFirestore(token: String) {
        mFirestore = FirebaseFirestore.getInstance()
        Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] sendFcmTokenToFirebtore() token: $token")
        Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] mAuth.currentUser.email: ${mAuth.currentUser?.email}")
        Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] mAuth.currentUser.email: ${mAuth.currentUser?.uid}")
        mFirestore.collection("Accounts/${mAuth.currentUser?.uid}")
            .document()
            .update("fcmToken", token)
            ?.addOnSuccessListener {
                Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] update fcm token: $token to ${mAuth.currentUser?.email}")
            }?.addOnFailureListener {
                Log.d(CLIENT_TAG, "[CustomFirebaseMessagingService] update fcm token Failed: ${it.message}")
            }
    }
}