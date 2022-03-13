package com.developers.healtywise.presentation.main.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.domin.useCase.createPostUseCase.CreatePostUseCase
import com.developers.healtywise.domin.useCase.getDoctorUseCase.GetDoctorUseCase
import com.developers.healtywise.domin.useCase.getPostsUseCase.GetPostUseCase
import com.developers.healtywise.presentation.main.home.state.HomeCreatePostUiState
import com.developers.healtywise.presentation.main.home.state.HomeGetPostUiState
import com.developers.healtywise.presentation.main.search.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getDoctorUseCase: GetDoctorUseCase,
) : ViewModel() {

    private val _getDoctorState = Channel<SearchUiState>()
    val getDoctorState: Flow<SearchUiState> = _getDoctorState.receiveAsFlow()

    fun getDoctorsOrNormalUsers(
        query: String="",
        currentUserId:String,
        userDoctor:Boolean=false
    ) {
        getDoctorUseCase(query,currentUserId,userDoctor).onEach {
            when (it) {
                is Resource.Success -> {
                    _getDoctorState.send(SearchUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _getDoctorState.send(SearchUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _getDoctorState.send(SearchUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }


}