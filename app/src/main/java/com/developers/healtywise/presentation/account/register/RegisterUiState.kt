package com.developers.healtywise.presentation.account.register

import com.developers.healtywise.domin.models.account.User


data class RegisterUiState(
    val isLoading: Boolean = false,
    val data: User? = null,
    val error: String?=null,
)