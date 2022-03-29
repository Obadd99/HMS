package com.developers.healtywise.domin.useCase.saveResult


import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.safeCall
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.Result
import com.developers.healtywise.domin.repository.AccountRepository
import com.developers.healtywise.domin.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveResultUseCase @Inject constructor(
    private val mainRepository: MainRepository,
) {


    operator fun invoke(
       userId:String,
       result: Result
    ): Flow<Resource<Any>> = flow {
        emit(Resource.Loading())
        val result = safeCall {
            val registerData = mainRepository.saveRecentResult(userId, result)
            Resource.Success(registerData)
        }
        emit(result)
    }


}