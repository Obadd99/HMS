package com.developers.healtywise.presentation.main.message.state

import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.ChatMessage
import com.developers.healtywise.domin.models.main.Post

data class GetRecentMessageUiState(
    val isLoading: Boolean = false,
    val data: List<ChatMessage>? = null,
    val error: String?=null,
)