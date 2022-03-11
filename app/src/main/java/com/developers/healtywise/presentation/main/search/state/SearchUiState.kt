package com.developers.healtywise.presentation.main.search.state

import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.Post

data class SearchUiState(
    val isLoading: Boolean = false,
    val data: List<User>? = null,
    val error: String?=null,
)