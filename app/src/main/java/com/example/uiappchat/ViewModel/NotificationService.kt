package com.example.uiappchat.ViewModel

import android.util.Log
import com.example.uiappchat.Model.NetworkModule
import com.example.uiappchat.Model.NotiRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object NotificationService {
    private const val TAG = "NotificationService"

    fun sendNotification(token: String, title: String, body: String, chatId: String, senderId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = NotiRequest(token, title, body, chatId, senderId)
                val response = NetworkModule.apiService.sendNoti(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Notification sent successfully")
                    } else {
                        Log.e(TAG, "Failed to send notification: ${response.code()} - ${response.message()}")
                        Log.e(TAG, "Error body: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception when sending notification", e)
            }
        }
    }
}