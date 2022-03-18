package com.developers.healtywise.presentation.account.editProfile

import com.developers.healtywise.domin.models.account.User

data class UpdateProfileUiState(
    val isLoading: Boolean = false,
    val data: User? = null,
    val error: String?=null,
)