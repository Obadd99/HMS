package com.developers.healtywise.presentation.main.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.developers.healtywise.common.helpers.Resource
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.domin.useCase.createPostUseCase.CreatePostUseCase
import com.developers.healtywise.domin.useCase.getPostsUseCase.GetPostUseCase
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
class HomeViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val getPostUseCase: GetPostUseCase
) : ViewModel() {
    private val _createPostState = Channel<HomeCreatePostUiState>()
    val createPostStateCreatePost: Flow<HomeCreatePostUiState> = _createPostState.receiveAsFlow()

    private val _getPostState = Channel<HomeGetPostUiState>()
    val getPostState: Flow<HomeGetPostUiState> = _getPostState.receiveAsFlow()

    fun createPost(
        text: String,
    ) {
        createPostUseCase(text).onEach {
            when (it) {
                is Resource.Success -> {
                    getPosts()
                    _createPostState.send(HomeCreatePostUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _createPostState.send(HomeCreatePostUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _createPostState.send(HomeCreatePostUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getPosts() {
        getPostUseCase().onEach {
            when (it) {
                is Resource.Success -> {
                    _getPostState.send(HomeGetPostUiState(data = it.data!!))
                }
                is Resource.Error -> {
                    _getPostState.send(HomeGetPostUiState(error = it.message?:""))
                }
                is Resource.Loading -> {
                    _getPostState.send(HomeGetPostUiState(isLoading = true))
                }
            }
        }.launchIn(viewModelScope)
    }

}