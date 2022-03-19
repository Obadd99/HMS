package com.developers.healtywise.app

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.getstream.chat.android.pushprovider.firebase.FirebaseMessagingDelegate

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Update device's token on Stream backend
        try {
            FirebaseMessagingDelegate.registerFirebaseToken(token)
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        try {
            if (FirebaseMessagingDelegate.handleRemoteMessage(message)) {
                // RemoteMessage was from Stream and it is already processed
            } else {
                // RemoteMessage wasn't sent from Stream and it needs to be handled by you
            }
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
        }
    }
}