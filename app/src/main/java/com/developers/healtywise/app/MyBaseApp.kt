package com.developers.healtywise.app

import android.app.Application
import com.developers.healtywise.common.helpers.utils.Constants.API_KEY
import dagger.hilt.android.HiltAndroidApp
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.ChatDomain

@HiltAndroidApp
class MyBaseApp:Application(){
    //
    override fun onCreate() {
        super.onCreate()

        // Step 1 - Set up the client for API calls and the domain for offline storage
        val client = ChatClient.Builder(API_KEY, this)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()
        ChatDomain.Builder(client, this).build()

//        FirebaseApp.initializeApp(applicationContext)
//        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC)
//        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
//        FirebaseMessaging.getInstance().token.addOnSuccessListener {
//            FirebaseService.token = it
//            Log.i(TAG, "onCreate: ${it}")
//            Log.e(TAG, "onCreate: ${it.toString()}", )
//        }
    }
}