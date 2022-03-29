package com.developers.healtywise.presentation.main.checkResult.state

import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.Post

data class SaveResultUiState(
    val isLoading: Boolean = false,
    val data: Any? = null,
    val error: String?=null,
)