package com.developers.healtywise.presentation.main.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.domin.useCase.getMessagesUseCase.GetMessagesUseCase
import com.developers.healtywise.domin.useCase.sendMessageUseCase.SendMessageUseCase
import com.developers.healtywise.presentation.main.chat.state.GetMessageUiState
import com.developers.healtywise.presentation.main.chat.state.SendMessageUiState
import com.developers.healtywise.presentation.main.home.state.HomeCreatePostUiState
import com.developers.healtywise.presentation.main.home.state.HomeGetPostUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class SendMessageViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase
) : ViewModel() {


    private val _sendMessageState = Channel<SendMessageUiState>()
    val sendMessagedState: Flow<SendMessageUiState> = _sendMessageState.receiveAsFlow()


    private val _getMessageState = Channel<GetMessageUiState>()
    val getMessageState: Flow<GetMessageUiState> = _getMessageState.receiveAsFlow()
    fun getMessage(imageProfile: String,senderId:String,receiverId: String) {
        getMessagesUseCase(imageProfile,senderId, receiverId).onEach {
            when (it) {
                is Resource.Success -> {
                    _getMessageState.send(GetMessageUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _getMessageState.send(GetMessageUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _getMessageState.send(GetMessageUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendMessage(
        message: String,
        receiverId: String
    ) {
        sendMessageUseCase(message,receiverId).onEach {
            Log.i(TAG, "sendMessage:${it.toString()} ")

            when (it) {
                is Resource.Success -> {
                    Log.i(TAG, "sendMessage: Success")
                    _sendMessageState.send(SendMessageUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    Log.i(TAG, "sendMessage:Error ${it.message}")

                    _sendMessageState.send(SendMessageUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _sendMessageState.send(SendMessageUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }

}