package com.example.uiappchat.ui.theme.UI

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.uiappchat.ViewModel.AuthViewModel
import com.example.uiappchat.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun AddFriendRequestList(userId: String, navController: NavController) {
    val database = FirebaseDatabase.getInstance().getReference("friend_requests").child(userId)
    val friendRequests = remember { mutableStateListOf<Map<String, String>>() }
    val isRefreshing = remember { mutableStateOf(false) }

    // Define the listener outside the DisposableEffect so it can be accessed by both DisposableEffect and onRefresh
    val listener = rememberUpdatedState(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            friendRequests.clear()
            for (request in snapshot.children) {
                val senderId = request.key ?: continue
                val senderInfo = request.getValue(object : GenericTypeIndicator<Map<String, String>>() {})
                if (senderInfo != null) {
                    val updatedInfo = senderInfo.toMutableMap()
                    updatedInfo["senderId"] = senderId
                    friendRequests.add(updatedInfo)
                }
            }
            isRefreshing.value = false // Stop refreshing when data is loaded
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Error reading data: ${error.message}")
            isRefreshing.value = false
        }
    })

    DisposableEffect(userId) {
        // Attach the listener to the database
        database.addValueEventListener(listener.value)

        onDispose {
            // Remove the listener when the composable is disposed
            database.removeEventListener(listener.value)
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing.value),
        onRefresh = {
            // Set isRefreshing to true when starting to refresh
            isRefreshing.value = true
            // Reload the data by re-triggering the listener manually
            database.removeEventListener(listener.value) // Remove previous listener to avoid duplicate calls
            database.addValueEventListener(listener.value) // Re-add the listener to refresh data
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(friendRequests) { request ->
                AddFriend(
                    senderId = request["senderId"] ?: "",
                    senderName = request["senderName"] ?: "Unknown",
                    senderEmail = request["senderEmail"] ?: "Unknown",
                    userId = userId,
                    navController = navController
                )
            }
        }
    }
}


@Composable
fun AddFriend(
    senderId: String,
    senderName: String,
    senderEmail: String,
    userId: String,
    navController: NavController // Add NavController to navigate to the chat screen
) {
    val database = FirebaseDatabase.getInstance()
    val context = LocalContext.current // For showing Toast messages
    val isProcessing = remember { mutableStateOf(false) } // To prevent multiple clicks

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.wukong),
            contentDescription = "ảnh",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
        ) {
            Text(
                text = senderName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF)
            )
            Text(
                text = senderEmail,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (isProcessing.value) return@Button
                        isProcessing.value = true

                        // Generate a unique chatId
                        val chatId = if (userId < senderId) "$userId$senderId" else "$senderId$userId"

                        // References to Firebase nodes
                        val friendRequestsRef = database.getReference("friend_requests").child(userId).child(senderId)
                        val friendsRefUser = database.getReference("friends").child(userId).child(senderId)
                        val friendsRefSender = database.getReference("friends").child(senderId).child(userId)
                        val chatRef = database.getReference("chats").child(chatId)

                        // Check if a chat already exists
                        chatRef.get().addOnSuccessListener { chatSnapshot ->
                            if (chatSnapshot.exists()) {
                                // Chat already exists, just remove the friend request and add to friends
                                friendRequestsRef.removeValue()
                                friendsRefUser.setValue(true)
                                friendsRefSender.setValue(true)

                                // Navigate to the existing chat
                                navController.navigate("screen3?chatId=$chatId&currentUserId=$userId")
                                isProcessing.value = false
                            } else {
                                // Fetch current user's info
                                val userRef = database.getReference("users").child(userId)
                                userRef.get().addOnSuccessListener { userSnapshot ->
//                                    val currentUserEmail = userSnapshot.child("email").getValue(String::class.java) ?: ""
                                    val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                                    val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                                    val currentUserName = "$firstName $lastName"
                                    currentUserName.trim()

                                    // Remove the friend request
                                    friendRequestsRef.removeValue()

                                    // Add to friends list (store minimal data)
                                    friendsRefUser.setValue(true)
                                    friendsRefSender.setValue(true)

                                    // Create a new chat room with the correct structure
                                    val usersMap = mapOf(
                                        userId to true,
                                        senderId to true
                                    )
                                    chatRef.child("users").setValue(usersMap)

                                    // Create a welcome message
                                    val welcomeMessage = mapOf(
                                        "senderId" to userId,
                                        "content" to "Chào mừng bạn đến với cuộc trò chuyện!",
                                        "timestamp" to ServerValue.TIMESTAMP
                                    )
                                    chatRef.child("messages").push().setValue(welcomeMessage)
                                        .addOnSuccessListener {
                                            Log.d("Firebase", "Chat room $chatId created with $senderName")
                                            // Navigate to the new chat
                                            navController.navigate("screen3?chatId=$chatId&currentUserId=$userId")
                                            isProcessing.value = false
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firebase", "Error creating chat room: ${e.message}")
                                            Toast.makeText(context, "Lỗi khi tạo phòng chat", Toast.LENGTH_SHORT).show()
                                            isProcessing.value = false
                                        }
                                }.addOnFailureListener { e ->
                                    Log.e("Firebase", "Error fetching user info: ${e.message}")
                                    Toast.makeText(context, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show()
                                    isProcessing.value = false
                                }
                            }
                        }.addOnFailureListener { e ->
                            Log.e("Firebase", "Error checking chat existence: ${e.message}")
                            Toast.makeText(context, "Lỗi khi kiểm tra phòng chat", Toast.LENGTH_SHORT).show()
                            isProcessing.value = false
                        }
                    },
                    shape = RectangleShape,
                    modifier = Modifier
                        .width(120.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    enabled = !isProcessing.value // Disable button while processing
                ) {
                    Text(text = "Chấp nhận")
                }

                Button(
                    onClick = {
                        // Reject the friend request
                        database.reference.child("friend_requests").child(userId).child(senderId)
                            .removeValue()
                            .addOnSuccessListener {
                                Log.d("Firebase", "Rejected friend request from $senderName")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Error rejecting friend request: ${e.message}")
                                Toast.makeText(context, "Lỗi khi từ chối lời mời", Toast.LENGTH_SHORT).show()
                            }
                    },
                    shape = RectangleShape,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAF5858))
                ) {
                    Text(text = "Từ chối")
                }
            }
        }
    }
}

@Composable
fun Suggestions(navController: NavController, authViewModel: AuthViewModel = viewModel()) {

    val userId by authViewModel.userId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B263B))
    ) {
        userId?.let { AddandSearchScreen3(navController, it) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Text(
                text = "All",
                fontSize = 20.sp,
                modifier = Modifier
                    .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(22.dp))
                    .padding(8.dp)
                    .clickable { navController.navigate("screen4?userId=$userId") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Suggestions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(22.dp))
                    .padding(8.dp)
            )
        }

        // Thêm weight(1f) để giữ taskbar luôn ở dưới
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            userId?.let { AddFriendRequestList(it, navController) }
        }

        userId?.let { TaskBarsScreen3(navController, it) } // Luôn hiển thị ở cuối màn hình
    }
}