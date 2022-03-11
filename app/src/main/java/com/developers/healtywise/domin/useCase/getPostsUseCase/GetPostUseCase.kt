package com.developers.healtywise.domin.useCase.getPostsUseCase


import com.developers.healtywise.common.helpers.Resource
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

class GetPostUseCase @Inject constructor(
    private val mainRepository: MainRepository,
    private val formatter: SimpleDateFormat,
    private val date: Date,
) {


    operator fun invoke(
    ): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())
        val result = safeCall {
            val createData = mainRepository.getPosts().onEach {
//                it.currentPostTime=formatter.format(date.setTime(it.date))
                it.currentPostTime=formatter.format(Date(it.date))
            }
            Resource.Success(createData)
        }
        emit(result)
    }


}