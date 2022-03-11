package com.developers.healtywise.domin.models.main

import com.google.firebase.firestore.Exclude

data class Post(
    val id: String="",
    val authorUid: String="",
    val text: String="",
    val date: Long=0L,
    @get:Exclude var authorUsername: String = "",
    @get:Exclude var authorProfilePictureUrl: String = "",
    @get:Exclude var currentPostTime: String = ""
    )