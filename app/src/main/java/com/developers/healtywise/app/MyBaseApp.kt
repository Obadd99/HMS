package com.developers.healtywise.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyBaseApp:Application(){
    //
    override fun onCreate() {
        super.onCreate()

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