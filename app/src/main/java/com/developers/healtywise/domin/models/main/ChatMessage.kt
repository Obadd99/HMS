package com.developers.healtywise.domin.models.main

import com.google.firebase.firestore.Exclude

data class ChatMessage(
    val id:String="",
    val sendId:String="",
    val receiverId:String="",
    var message:String="",
    val date: Long=0L,
    @get:Exclude var receiverProfilePictureUrl: String = "",
    @get:Exclude var dateTimeMessage: String = ""
)
