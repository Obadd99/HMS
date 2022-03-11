package com.developers.healtywise.presentation.main.home.state

import com.developers.healtywise.domin.models.account.User

data class HomeCreatePostUiState(
    val isLoading: Boolean = false,
    val data: Any? = null,
    val error: String?=null,
)