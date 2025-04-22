package com.example.uiappchat.Model

data class MessageData(
    val name: String,
    val content: String,
    val time: String,
    val unreadCount: Int,
    val friendId: String = "" // Add this to know which friend to chat with when clicking
)
