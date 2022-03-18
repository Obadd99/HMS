package com.developers.healtywise.domin.useCase.GetRecentMessageUseCase


import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.safeCall
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.ChatMessage
import com.developers.healtywise.domin.models.main.Post
import com.developers.healtywise.domin.repository.AccountRepository
import com.developers.healtywise.domin.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetRecentMessageUseCase @Inject constructor(
    private val mainRepository: MainRepository,
    private val formatter: SimpleDateFormat,
) {


    operator fun invoke(
        userId:String
    ): Flow<Resource<List<ChatMessage>>> = flow {
        emit(Resource.Loading())
        val result = safeCall {
            val createData = mainRepository.getRecentConversations(userId)
            Resource.Success(createData)
        }
        emit(result)
    }


}