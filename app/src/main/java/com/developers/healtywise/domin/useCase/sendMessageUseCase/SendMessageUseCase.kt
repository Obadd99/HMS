package com.developers.healtywise.domin.useCase.sendMessageUseCase


import android.util.Log
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.common.helpers.utils.safeCall
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.Post
import com.developers.healtywise.domin.repository.AccountRepository
import com.developers.healtywise.domin.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val mainRepository: MainRepository,
) {

    operator fun invoke(
        message: String,
        receiverId: String
    ): Flow<Resource<Any>> = flow {
        emit(Resource.Loading())
        val result = safeCall {
            Log.i(TAG, "invoke:sendMessage ")
            val createMessageData = mainRepository.sendMessage(message, receiverId)
            Resource.Success(createMessageData)
        }
        emit(result)
    }


}