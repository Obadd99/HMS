package com.developers.healtywise.domin.repository

import android.app.Activity
import com.developers.healtywise.domin.models.account.User
import com.google.firebase.auth.PhoneAuthProvider

interface AccountRepository {

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        mobile: String,
        password: String,
        birthdate: String,
        doctor: Boolean,
        male: Boolean,
        imageUri:String?
    ): User
    suspend fun login(email: String,password:String): User
}