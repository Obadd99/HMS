package com.developers.healtywise.domin.models.main

import com.developers.healtywise.domin.models.account.User
import com.google.firebase.firestore.Exclude

data class ChatMessage(
    val id:String="",
    val sendId:String="",
    val receiverId:String="",
    var message:String="",
    val date: Long=0L,
    @get:Exclude var userReceiverData: User? = null,
    @get:Exclude var dateTimeMessage: String = ""
)
