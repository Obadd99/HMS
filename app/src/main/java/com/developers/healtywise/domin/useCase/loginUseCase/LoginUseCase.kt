package com.developers.healtywise.domin.useCase.loginUseCase


import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.safeCall
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {


    operator fun invoke(
        email: String,
        password: String,
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        val result = safeCall {
            val registerData = accountRepository.login(email, password)
            Resource.Success(registerData)
        }
        emit(result)
    }


}