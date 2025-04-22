package com.example.uiappchat.ui.theme.UI

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.uiappchat.Model.MessageData
import com.example.uiappchat.ViewModel.addFriend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ListFriendScreen1(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val userId by authViewModel.userId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B263B))
    ) {
        AddandSearch(navController, userId)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            userId?.let { MessageList(navController, it) } // Truyền userId thay vì currentUserId
        }
        userId?.let { TaskBars(navController, it) }
    }
}
    @Composable
    fun AddandSearch(navController: NavController, userId: String?){
        var isSearching by remember { mutableStateOf(false) }
        var email by remember { mutableStateOf("") }
        val context = LocalContext.current

        // Thêm trạng thái để theo dõi số lượng lời mời kết bạn
        var friendRequestsCount by remember { mutableIntStateOf(0) }
        // Lấy số lượng lời mời kết bạn từ Firebase
        LaunchedEffect(userId) {
            userId?.let { uid ->
                val friendRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests").child(uid)
                friendRequestsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        friendRequestsCount = snapshot.childrenCount.toInt()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Lỗi khi lấy số lượng lời mời kết bạn: ${error.message}")
                    }
                })
            }
        }

        Box( modifier = Modifier.fillMaxWidth()) {
            Image(
                painterResource(id = R.drawable.mepmanhinhtrai),
                contentDescription = "mép phải",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth())

            Row(modifier = Modifier.fillMaxWidth()
                .padding(top = 50.dp).padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add friend",
                    modifier = Modifier
                        .width(27.dp)
                        .height(27.dp)
                        .clickable {
                            if (email.isNotEmpty()) {
                                addFriend.searchUserByEmail(email) { user ->
                                    if (user?.userId != null) {
                                        Log.d("FriendRequest", "Tìm thấy user: ${user.userId}")

                                        val senderId = FirebaseAuth.getInstance().currentUser?.uid // ID người gửi
                                        val senderEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

                                        if (senderId.isNullOrEmpty() || senderEmail.isEmpty()) {
                                            Log.e("FriendRequest", "Lỗi: Không lấy được thông tin người gửi!")
                                            Toast.makeText(context, "Lỗi: Không lấy được thông tin người gửi!", Toast.LENGTH_SHORT).show()
                                            return@searchUserByEmail
                                        }

                                        Log.d("FirestoreCheck", "Sender ID: $senderId")
                                        Log.d("FirestoreCheck", "Sender Email: $senderEmail")

                                        val db = FirebaseFirestore.getInstance()
                                        val formattedEmail = senderEmail

                                        // Lấy thông tin người gửi từ Firestore
                                        db.collection("users").document(formattedEmail).get()
                                            .addOnSuccessListener { document ->
                                                if (document.exists()) {
                                                    val senderFirstName = document.getString("firstName") ?: "Không có tên"
                                                    val senderLastName = document.getString("lastName") ?: ""
                                                    val senderName = "$senderFirstName $senderLastName".trim()

                                                    val receiverId = user.userId // ID người nhận

                                                    addFriend.sendFriendRequest(senderId, senderName, senderEmail, receiverId)
                                                    Toast.makeText(context, "Đã gửi lời mời kết bạn!", Toast.LENGTH_SHORT).show()
                                                    Log.d("FriendRequest", "Lời mời kết bạn đã gửi thành công!")
                                                } else {
                                                    Log.e("FriendRequest", "Không tìm thấy thông tin người gửi trong Firestore! Email: $formattedEmail")
                                                    Toast.makeText(context, "Không tìm thấy thông tin người gửi!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("FriendRequest", "Lỗi khi lấy thông tin người gửi từ Firestore: ${e.message}", e)
                                                Toast.makeText(context, "Lỗi khi lấy thông tin người gửi!", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Log.e("FriendRequest", "Không tìm thấy user hoặc userId bị null!")
                                        Toast.makeText(context, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show()
                            }
                        },
                    tint = Color(0xFFFFFFFF)

                )
                if(isSearching) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Nhập email") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .width(27.dp)
                        .height(27.dp)
                        .clickable { isSearching = !isSearching },
                    tint = Color(0xFFFFFFFF)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()
                .padding(top = 120.dp, start = 45.dp, end = 45.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Friends",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color =  Color(0xFFFCFBFE),
                    modifier = Modifier.clickable { navController.navigate("screen2?userId=$userId") }
                )
                Box {
                    Text(text = "Contacts",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF).copy(alpha = 0.25f),
                        modifier = Modifier.clickable { navController.navigate("screen4?userId=$userId") }
                    )
                    if (friendRequestsCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .offset(x = 5.dp, y = (-5).dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MessageList(navController: NavController,
                    userId: String) {
        val database = FirebaseDatabase.getInstance()
        val chatList = remember { mutableStateListOf<MessageData>() }
        val isLoading = remember { mutableStateOf(true) }
        val chatIds = remember { mutableStateListOf<String>() } // To store chat IDs


        DisposableEffect(userId) {
            val chatRef = database.reference.child("chats")

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isLoading.value = true
                    chatList.clear()
                    chatIds.clear()

                    Log.d("Firebase", "Chats snapshot: ${snapshot.childrenCount} chats found")

                    if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                        Log.d("Firebase", "No chats found for user $userId")
                        isLoading.value = false
                        return
                    }

                    val tempList = mutableListOf<MessageData>()
                    val tempChatIds = mutableListOf<String>()
                    val remainingTasks = mutableStateOf(snapshot.childrenCount.toInt())

                    snapshot.children.forEach { chatSnapshot ->
                        val chatId = chatSnapshot.key ?: return@forEach
                        Log.d("Firebase", "Processing chat: $chatId")

                        val usersNode = chatSnapshot.child("users")
                        if (!usersNode.exists()) {
                            Log.d("Firebase", "Chat $chatId has no users node")
                            remainingTasks.value -= 1
                            if (remainingTasks.value == 0) {
                                isLoading.value = false
                            }
                            return@forEach
                        }

                        val userIds = usersNode.children.mapNotNull { it.key }
                        Log.d("Firebase", "Chat $chatId has users: $userIds")

                        if (userIds.contains(userId)) {
                            val friendId = userIds.firstOrNull { it != userId }
                            if (friendId == null) {
                                Log.d("Firebase", "Couldn't find friend ID in chat $chatId")
                                remainingTasks.value -= 1
                                if (remainingTasks.value == 0) {
                                    isLoading.value = false
                                }
                                return@forEach
                            }

                            Log.d("Firebase", "Found friend ID: $friendId in chat $chatId")

                            val messagesNode = chatSnapshot.child("messages")
                            if (!messagesNode.exists()) {
                                Log.d("Firebase", "No messages in chat $chatId")
                            }

                            val lastMessageSnapshot = messagesNode.children.lastOrNull()
                            val lastMessage = lastMessageSnapshot?.child("text")?.value?.toString() ?: "Chưa có tin nhắn"
                            val timestamp = lastMessageSnapshot?.child("timestamp")?.value?.toString() ?: ""

                            // Tính unreadCount bằng timestamp > lastSeen
                            val lastSeenSnapshot = chatSnapshot.child("lastSeen").child(userId)
                            val lastSeen = lastSeenSnapshot.getValue(Long::class.java) ?: 0L

                            val unreadCount = messagesNode.children.count { msg ->
                                val msgTimestamp = msg.child("timestamp").getValue(Long::class.java) ?: 0L
                                val senderId = msg.child("senderId").getValue(String::class.java) ?: ""
                                msgTimestamp > lastSeen && senderId != userId
                            }

                            database.reference.child("users").child(friendId).get().addOnSuccessListener { userSnapshot ->
                                val firstName = userSnapshot.child("firstName").value?.toString() ?: ""
                                val lastName = userSnapshot.child("lastName").value?.toString() ?: ""
                                val friendName = "$firstName $lastName"

                                Log.d("Firebase", "Friend name: $friendName")

                                tempList.add(
                                    MessageData(
                                    name = friendName.ifEmpty { "Người dùng" },
                                    content = lastMessage,
                                    time = formatTimestamp(timestamp),
                                    unreadCount = unreadCount,
                                        friendId = friendId
                                )
                                )
                                tempChatIds.add(chatId) // Store the chatId

                                chatList.clear()
                                chatList.addAll(tempList)
                                chatIds.clear()
                                chatIds.addAll(tempChatIds)
                                remainingTasks.value -= 1
                                if (remainingTasks.value == 0) {
                                    isLoading.value = false
                                }
                            }.addOnFailureListener { e ->
                                Log.e("Firebase", "Failed to get user data: ${e.message}")
                                remainingTasks.value -= 1
                                if (remainingTasks.value == 0) {
                                    isLoading.value = false
                                }
                            }
                        } else {
                            Log.d("Firebase", "User $userId is not part of chat $chatId")
                            remainingTasks.value -= 1
                            if (remainingTasks.value == 0) {
                                isLoading.value = false
                            }
                        }
                    }

                    if (remainingTasks.value == 0) {
                        Log.d("Firebase", "Finished processing chats, chatList size: ${chatList.size}")
                        isLoading.value = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error loading chat list: ${error.message}")
                    isLoading.value = false
                }
            }

            chatRef.addValueEventListener(listener)
            onDispose { chatRef.removeEventListener(listener) }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (chatList.isEmpty()) {
                Text(
                    text = "Chưa có cuộc trò chuyện nào",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(chatList) { index, message ->
                        List(
                            message = message,
                            navController = navController,
                            chatId = chatIds.getOrNull(index) ?: "", // Pass the corresponding chatId
                            currentUserId = userId, // Pass the userId as currentUserId
                            friendId = message.friendId
                            )
                    }
                }
            }
        }
    }

    // Helper function to format timestamp
    fun formatTimestamp(timestamp: String): String {
        if (timestamp.isEmpty()) return ""
        try {
            val time = timestamp.toLong()
            val date = Date(time)
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(date)
        } catch (e: Exception) {
            return ""
        }
    }

    // Keep your original List composable exactly as is
    @Composable
    fun List(message: MessageData,
             navController: NavController,
             chatId: String,
             currentUserId: String,
             friendId: String) {

        val isOnline = remember { mutableStateOf(false) }

        LaunchedEffect(friendId) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(friendId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isOnline.value = snapshot.child("online").getValue(Boolean::class.java) ?: false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Không thể lấy trạng thái online: ${error.message}")
                }
            })
        }

        Box(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0077B6))
                        .clickable { navController.navigate("screen3?chatId=$chatId&currentUserId=$currentUserId") }, // Pass chatId and currentUserId
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(id = R.drawable.img_8),
                        contentDescription = "ảnh",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(45.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { navController.navigate("screen3?chatId=$chatId&currentUserId=$currentUserId") }
                    )
                    Column {
                        Text(
                            text = message.name,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = message.content, color = Color.White)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 10.dp),
                    ) {
                        Text(
                            text = message.time,
                            fontSize = 10.sp,
                            color = Color(0xFFFFFFFF),
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.End)
                                .clip(CircleShape)
                                .background(if (isOnline.value) Color(0xFF00B912) else Color.Gray)
                        )
                    }
                }
            }
            if(message.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = message.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }


    @Composable
    fun TaskBars(navController: NavController,userId: String) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painterResource(id = R.drawable.ellipse),
                contentDescription = "taskbars",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(
                    horizontal = 60.dp)
            ) {
                Image(
                    painterResource(id = R.drawable.img_6),
                    contentDescription = "chat",
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF))
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painterResource(id = R.drawable.img_7),
                    contentDescription = "chat",
                    modifier = Modifier.size(40.dp)
                        .clickable { navController.navigate("camerascreen?userId=$userId") },
                    colorFilter = ColorFilter.tint(Color(0x8000BCD4))
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painterResource(id = R.drawable.img_8),
                    contentDescription = "chat",
                    modifier = Modifier.size(40.dp)
                        .clickable { navController.navigate("profilescreen?userId=$userId") },
                    colorFilter = ColorFilter.tint(Color(0x8000BCD4))
                )
            }
        }
    }
