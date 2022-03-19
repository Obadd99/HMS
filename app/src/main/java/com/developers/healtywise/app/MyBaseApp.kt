package com.developers.healtywise.app

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.developers.healtywise.common.helpers.utils.Constants.ACTION_NEW_MESSAGE_SENT
import com.developers.healtywise.common.helpers.utils.Constants.API_KEY
import com.developers.healtywise.presentation.activities.MainActivity
import dagger.hilt.android.HiltAndroidApp
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.client.notifications.handler.PushDeviceGenerator
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.pushprovider.firebase.FirebasePushDeviceGenerator

@HiltAndroidApp
class MyBaseApp:Application(){
    //
    override fun onCreate() {
        super.onCreate()
        val notificationConfig = NotificationConfig(
                pushDeviceGenerators = listOf(FirebasePushDeviceGenerator())

        )
        val notificationHandler = NotificationHandlerFactory.createNotificationHandler(
            context = this,
            newMessageIntent = {
                    messageId: String,
                    channelType: String,
                    channelId: String,
                ->
                // Return the intent you want to be triggered when the notification is clicked
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("channelId",channelId)
                intent.putExtra("channelType",channelType)
                intent.action = ACTION_NEW_MESSAGE_SENT
                intent
            }
        )
        // Step 1 - Set up the client for API calls and the domain for offline storage
        val client = ChatClient.Builder(API_KEY, this)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .notifications(notificationConfig, notificationHandler)
            .build()
        ChatDomain.Builder(client, this).build()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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