package com.example.uiappchat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCMService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New token: $token")
        saveTokenToRealtime(token)
    }

    private fun saveTokenToRealtime(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.e(TAG, "Cannot save token - user not logged in")
            return
        }

        Log.d(TAG, "Saving token for user $uid: $token")
        FirebaseDatabase.getInstance()
            .getReference("users/$uid/fcmToken")
            .setValue(token)
            .addOnSuccessListener { Log.d(TAG, "Token saved successfully") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to save token", e) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        // Log notification details
        remoteMessage.notification?.let {
            Log.d(TAG, "Notification Title: ${it.title}")
            Log.d(TAG, "Notification Body: ${it.body}")
        }

        // Log data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${remoteMessage.data}")
        }

        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val data = remoteMessage.data

        showNotification(title, body, data["chatId"], data["senderId"])
    }

    private fun ensureChannelCreated(nm: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId   = "chat_notifications"
            val name        = "Chat Messages"
            val description = "Channel for chat message notifications"
            val importance  = NotificationManager.IMPORTANCE_HIGH

            // Nếu channel đã tồn tại, create lại sẽ bỏ qua
            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = description
            }
            nm.createNotificationChannel(channel)
        }
    }

    private fun showNotification(
        title: String?, body: String?,
        chatId: String?, senderId: String?
    ) {
        val channelId = "chat_notifications"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1) Đảm bảo channel đã được tạo
        ensureChannelCreated(nm)

        // 2) Chuẩn bị Intent để deep‑link vào chat screen
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("chatId", chatId)
            putExtra("senderId", senderId)
        }
        val pi = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3) Build notification
        val notif = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_messages)     // icon bạn đã thêm trong drawable
            .setContentTitle(title ?: "Tin nhắn mới")
            .setContentText(body ?: "")
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        // 4) Hiển thị
        nm.notify(System.currentTimeMillis().toInt(), notif)
    }

}


