package com.developers.healtywise.domin.repository

import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.ChatMessage
import com.developers.healtywise.domin.models.main.Post
import com.developers.healtywise.domin.models.main.Result

interface MainRepository {
    suspend fun createPost(text:String): Any
    suspend fun getUsers(username:String,userDoctor:Boolean):List<User>
    suspend fun getUser(uid:String):User
    suspend fun getPosts():List<Post>
    suspend fun sendMessage(message:String, receiverId:String):Any
    suspend fun getMessage(senderId:String,receiverId: String):List<ChatMessage>
    suspend fun getRecentConversations(userId: String):List<ChatMessage>
    suspend fun editProfile(user: User):User
    suspend fun saveRecentResult(userId: String,result: Result):Any
}