package com.developers.healtywise.presentation.account.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.useCase.registerUseCase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _registerState = Channel<RegisterUiState>()
    val registerStateRegister: Flow<RegisterUiState> = _registerState.receiveAsFlow()


    fun register(
        user: User,
        password: String,
        ) {
        registerUseCase(user.firstName, user.lastName,user.email, user.mobile, password, user.birthDate, user.doctor,user.male,user.imageProfileUploaded).onEach {
            when (it) {
                is Resource.Success -> {
                    _registerState.send(RegisterUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _registerState.send(RegisterUiState(error = it.message ?: ""))
                }
                is Resource.Loading -> {
                    _registerState.send(RegisterUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }


}