package com.developers.healtywise.common.helpers.utils


import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object Constants {
    const val TAG = "GAMALRAGAB"

    const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS: Int = 123
    const val REQUEST_CODE_CHOOSE_TYPE_PERMISSIONS: Int = 1234
    const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSIONS: Int = 127
    const val ACCEPT_PAYMENT_REQUEST: Int = 217
    const val REQUEST_CODE_CONTACTS_PERMISSIONS = 717
    const val REQUEST_CODE_CAMERA_PERMISSIONS = 111
    const val REQUEST_CODE_LOCATION_PERMISSIONS: Int=555
    const val ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT: String = "ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT"
    const val HOLDER_ICON="https://cdn-icons-png.flaticon.com/512/149/149071.png"

    const val USERS_INFO_FILE: String = "USER_INFO"
    const val USERS: String = "Users"
    const val POSTS: String = "Posts"
    const val MESSAGES: String = "Chats"



    const val BASE_URL = ""



    const val BASE_URL_NOTIFICATION = "https://fcm.googleapis.com"
    const val SERVER_KEY =
        "AAAAfJQP7zE:APA91bGVBwaBpzMXCTXYQsDfYXqGQ-S28-YO6apvxE9NaaO8uJUIEdI04guneHC3qw2F3ExeqyrHdPbS96FJFbmlsnT-oKDnjuprs4sIuLzud0MXeBgSd52nOrfBQusF51572LNzmrOQ"
    const val CONTENT_TYPE = "application/json"
    const val TOPIC = "/topics/myTopic2"





    /*
     * for data store
     */
    val USER_TOKEN = stringPreferencesKey("USER_TOKEN")
    val CURRENT_PAYMENT_METHOD = stringPreferencesKey("CURRENT_PAYMENT_METHOD")
    val CURRENT_COLLECT_METHOD = stringPreferencesKey("CURRENT_COLLECT_METHOD")

    val USER_ID = stringPreferencesKey("USER_ID")
    val USER_FIRST_NAME = stringPreferencesKey("USER_FIRST_NAME")
    val USER_LAST_NAME = stringPreferencesKey("USER_LAST_NAME")
    val USER_EMAIL1 = stringPreferencesKey("USER_EMAIL1")
    val USER_MOBILE = stringPreferencesKey("USER_MOBILE")
    val USER_BIRTH_DATE = stringPreferencesKey("USER_BIRTH_DATE")
    val USER_IMAGE_PROFILE = stringPreferencesKey("USER_IMAGE_PROFILE")
    val USER_MALE = booleanPreferencesKey("USER_MALE")
    val USER_DOCTOR = booleanPreferencesKey("USER_DOCTOR")




    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L
    const val TIMER_UPDATE_INTERVAL = 500L
    const val SEARCH_TIME_DELAY = 500L



}