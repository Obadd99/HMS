package com.developers.healtywise.presentation.main.home.state

import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.Post

data class HomeGetPostUiState(
    val isLoading: Boolean = false,
    val data: List<Post>? = null,
    val error: String?=null,
)