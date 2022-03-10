package com.developers.healtywise.domin.useCase.registerUseCase


import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.safeCall
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {


    operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        mobile: String,
        password: String,
        birthdate: String,
        doctor: Boolean,
        male: Boolean,
        imageUri:String?
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        val result = safeCall {
            val registerData = accountRepository.register(firstName, lastName, email, mobile, password,birthdate, doctor,male,imageUri)
            Resource.Success(registerData)
        }
        emit(result)
    }


}