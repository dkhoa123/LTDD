package com.example.uiappchat.ViewModel

import android.util.Log
import com.example.uiappchat.Model.ChatMessage
import com.example.uiappchat.Model.NetworkModule
import com.example.uiappchat.Model.NotiRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object Chatting {

    fun sendMessage(chatId: String, senderId: String, text: String) {
        val database = FirebaseDatabase.getInstance()
            .getReference("chats/$chatId/messages")

        val messageId = database.push().key ?: return
        val message = ChatMessage(senderId, text, System.currentTimeMillis())

        // 1) Push tin nhắn lên Realtime Database
        database.child(messageId).setValue(message)
            .addOnSuccessListener {
                // 2) Lấy FCM token của friend
                val friendId = getFriendId(chatId, senderId) ?: return@addOnSuccessListener
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val tokenSnap = FirebaseDatabase.getInstance()
                            .getReference("users/$friendId/fcmToken")
                            .get()
                            .await()
                        val token = tokenSnap.getValue(String::class.java)
                        if (!token.isNullOrEmpty()) {
                            // 3) Gọi API server để gửi notification
                            sendNotification(token, chatId, senderId, text)
                        }
                    } catch (e: Exception) {
                        Log.e("Chatting", "Lỗi lấy token hoặc gửi noti", e)
                    }
                }
            }
    }

    private suspend fun sendNotification(
        token: String,
        chatId: String,
        senderId: String,
        text: String
    ) {
        try {
            val req = NotiRequest(
                token = token,
                title = "Tin nhắn mới",
                body = text,
                chatId = chatId,
                senderId = senderId
            )
            val resp = NetworkModule.apiService.sendNoti(req)
            if (!resp.isSuccessful) {
                Log.e("Chatting", "SendNoti error ${resp.code()}")
            }
        } catch (e: Exception) {
            Log.e("Chatting", "Exception sendNoti", e)
        }
    }

    fun listenForMessages(chatId: String, onNewMessage: (List<ChatMessage>) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("chats/$chatId/messages")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages =
                    snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                onNewMessage(messages)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching messages", error.toException())
            }
        })
    }

    fun getFriendId(chatId: String, currentUserId: String): String? {
        return if (chatId.startsWith(currentUserId)) {
            chatId.removePrefix(currentUserId)
        } else if (chatId.endsWith(currentUserId)) {
            chatId.removeSuffix(currentUserId)
        } else null
    }
}




