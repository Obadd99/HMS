package com.developers.healtywise.presentation.main.checkResult

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.domin.models.main.Result
import com.developers.healtywise.domin.useCase.createPostUseCase.CreatePostUseCase
import com.developers.healtywise.domin.useCase.getPostsUseCase.GetPostUseCase
import com.developers.healtywise.domin.useCase.saveResult.SaveResultUseCase
import com.developers.healtywise.presentation.main.checkResult.state.SaveResultUiState
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
class SaveResultViewModel @Inject constructor(
   private val saveResultUseCase: SaveResultUseCase
) : ViewModel() {
    private val _saveResultState = Channel<SaveResultUiState>()
    val saveResultState: Flow<SaveResultUiState> = _saveResultState.receiveAsFlow()



    fun saveResult(
       userId:String,
       result:Result
    ) {
        saveResultUseCase(userId,result).onEach {
            when (it) {
                is Resource.Success -> {
                    _saveResultState.send(SaveResultUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _saveResultState.send(SaveResultUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _saveResultState.send(SaveResultUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }


}