package com.developers.healtywise.presentation.main.chat.state

import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.ChatMessage

data class GetMessageUiState(
    val isLoading: Boolean = false,
    val data: List<ChatMessage>? = null,
    val error: String?=null,
)