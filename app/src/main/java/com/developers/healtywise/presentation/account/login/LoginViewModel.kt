package com.developers.healtywise.presentation.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.domin.useCase.loginUseCase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = Channel<LoginUiState>()
    val loginState: Flow<LoginUiState> = _loginState.receiveAsFlow()

    fun login(
        email: String,
        password: String,
    ) {
        loginUseCase( email, password).onEach {
            when (it) {
                is Resource.Success -> {
                    _loginState.send(LoginUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _loginState.send(LoginUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _loginState.send(LoginUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }

}