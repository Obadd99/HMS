package com.developers.healtywise.domin.useCase.createPostUseCase


import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.safeCall
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.repository.AccountRepository
import com.developers.healtywise.domin.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val mainRepository: MainRepository,
) {


    operator fun invoke(
        text: String
    ): Flow<Resource<Any>> = flow {
        emit(Resource.Loading())
        val result = safeCall {
            val createData = mainRepository.createPost(text)
            Resource.Success(createData)
        }
        emit(result)
    }


}