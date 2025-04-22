package com.example.uiappchat.Model

data class SignInUiState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null
)
