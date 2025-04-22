package com.example.uiappchat.Model

data class NotiRequest(
    val token: String,
    val title: String,
    val body: String,
    val chatId: String,
    val senderId: String
)
