package com.developers.healtywise.presentation.account.editProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.useCase.updateProfileUseCase.UpdateProfileUseCase
import com.developers.healtywise.presentation.account.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {


    private val _editProfileState = Channel<UpdateProfileUiState>()
    val editProfileState: Flow<UpdateProfileUiState> = _editProfileState.receiveAsFlow()

    fun editProfile(
       user: User
    ) {
        updateProfileUseCase(user).onEach {
            when (it) {
                is Resource.Success -> {
                    _editProfileState.send(UpdateProfileUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _editProfileState.send(UpdateProfileUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _editProfileState.send(UpdateProfileUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }


}