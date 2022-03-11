package com.developers.healtywise.data.netWork.repository

import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.data.netWork.account.AccountService
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.Post
import com.developers.healtywise.domin.repository.MainRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val account: AccountService,
    private val auth: FirebaseAuth,
) : MainRepository {
    override suspend fun createPost(text: String): Any = account.createPost(text)


    override suspend fun getUsers(username:String): List<User> = account.searchDoctorUser(username)

    override suspend fun getUser(uid: String): User = account.getUser(uid)

    override suspend fun getPosts(): List<Post> = account.getPosts()

}