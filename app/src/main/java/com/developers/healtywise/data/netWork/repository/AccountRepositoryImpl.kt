package com.developers.healtywise.data.netWork.repository

import android.app.Activity
import com.developers.healtywise.data.netWork.account.AccountService
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.repository.AccountRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val account: AccountService,
    private val auth: FirebaseAuth,
) : AccountRepository {
    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        mobile: String,
        password: String,
        birthdate: String,
        doctor: Boolean,
        male: Boolean,
        imageUri: String?,

        ): User =account.register(firstName, lastName, email, mobile, password,birthdate,doctor,male,imageUri)

    override suspend fun login(email: String, password: String): User =account.login(email,password)



}