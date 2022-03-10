package com.developers.healtywise.presentation.account.login

import com.developers.healtywise.domin.models.account.User

data class LoginUiState(
    val isLoading: Boolean = false,
    val data: User? = null,
    val error: String?=null,
)