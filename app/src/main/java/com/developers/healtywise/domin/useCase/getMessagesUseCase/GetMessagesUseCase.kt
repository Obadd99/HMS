package com.developers.healtywise.domin.useCase.getMessagesUseCase


import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.decodeByte
import com.developers.healtywise.common.helpers.utils.safeCall
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.ChatMessage
import com.developers.healtywise.domin.repository.AccountRepository
import com.developers.healtywise.domin.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val mainRepository: MainRepository,
    private val formatter: SimpleDateFormat,
    ) {


    operator fun invoke(imageProfile: String,senderId:String,receiverId: String): Flow<Resource<List<ChatMessage>>> = flow {
        emit(Resource.Loading())
        val result = safeCall {
            val createData = mainRepository.getMessage(senderId,receiverId).onEach {
                it.dateTimeMessage = formatter.format(Date(it.date))
                it.receiverProfilePictureUrl=imageProfile
                it.message=decodeByte(it.message)
            }
            Resource.Success(createData)
        }
        emit(result)
    }


}