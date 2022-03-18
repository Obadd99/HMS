package com.developers.healtywise.presentation.main.message

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.domin.useCase.GetRecentMessageUseCase.GetRecentMessageUseCase
import com.developers.healtywise.presentation.main.home.state.HomeCreatePostUiState
import com.developers.healtywise.presentation.main.home.state.HomeGetPostUiState
import com.developers.healtywise.presentation.main.message.state.GetRecentMessageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val getRecentMessageUseCase:GetRecentMessageUseCase
) : ViewModel() {

    private val _getRecentMessageUiState = Channel<GetRecentMessageUiState>()
    val getRecentMessageUiState: Flow<GetRecentMessageUiState> = _getRecentMessageUiState.receiveAsFlow()

    fun getRecentMessage(
        userId: String,
    ) {
        getRecentMessageUseCase(userId).onEach {
            when (it) {
                is Resource.Success -> {
                    _getRecentMessageUiState.send(GetRecentMessageUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _getRecentMessageUiState.send(GetRecentMessageUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _getRecentMessageUiState.send(GetRecentMessageUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }



}