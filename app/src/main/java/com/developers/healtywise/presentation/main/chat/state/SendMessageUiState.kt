package com.developers.healtywise.presentation.main.chat.state

import com.developers.healtywise.domin.models.account.User

data class SendMessageUiState(
    val isLoading: Boolean = false,
    val data: Any? = null,
    val error: String?=null,
)