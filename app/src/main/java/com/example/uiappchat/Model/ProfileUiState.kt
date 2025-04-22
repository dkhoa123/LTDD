package com.example.uiappchat.Model


data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSignedOut: Boolean = false,
    val errorMessage: String? = null
)