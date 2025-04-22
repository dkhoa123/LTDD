package com.example.uiappchat.ui.theme.UI

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uiappchat.R
import com.example.uiappchat.Model.ChatMessage
import com.example.uiappchat.ViewModel.Chatting
import com.example.uiappchat.ViewModel.Chatting.getFriendId
import com.example.uiappchat.ViewModel.Chatting.listenForMessages
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController


@Composable
fun BoxChatScreen2(navController: NavController, chatId: String, currentUserId: String) {
    val friendId = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(chatId, currentUserId) {
        friendId.value = getFriendId(chatId, currentUserId)
    }

    LaunchedEffect(Unit) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("chats/$chatId/lastSeen/$currentUserId")
        ref.setValue(System.currentTimeMillis())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B263B))
    ) {
        friendId.value?.let { id ->
            TopName(navController, friendId = id, currentUserId = currentUserId)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            ChatScreen(chatId, currentUserId)
        }
        FrameChatting(chatId, currentUserId)
    }
}

@Composable
fun TopName(navController: NavController,
            friendId: String,
            currentUserId: String) {
    val friendNameHo = remember { mutableStateOf("Người dùng") }
    val friendNameTen = remember { mutableStateOf("Người dùng") }
    val isOnline = remember { mutableStateOf(false) }

    // Lắng nghe trạng thái `online`
    LaunchedEffect(friendId) {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(friendId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendNameHo.value = snapshot.child("firstName").value?.toString() ?: "Người dùng"
                friendNameTen.value = snapshot.child("lastName").value?.toString() ?: "Người dùng"
                isOnline.value = snapshot.child("online").getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi tải dữ liệu: ${error.message}")
            }
        })
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painterResource(id = R.drawable.mepmanhinhfullbentren),
            contentDescription = "TopName",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { navController.navigate("screen2?userId=$currentUserId") },
                tint = Color.White
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = friendNameHo.value+" "+friendNameTen.value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isOnline.value) "Online" else "Offline",
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )
            }
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
            Image(
                painterResource(id = R.drawable.videocam),
                contentDescription = "videocam",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }
}

@Composable
fun ChatScreen(chatId: String, currentUserId: String) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(chatId) {
        listenForMessages(chatId) { newMessages ->
            messages.clear()
            messages.addAll(newMessages)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    // ✅ Xử lý click ra ngoài để ẩn bàn phím
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            state = listState
        ) {
            items(messages.indices.toList()) { index ->
                val message = messages[index]
                val previousTimestamp = if (index > 0) messages[index - 1].timestamp else null
                val middleTimeText = getMiddleTimestampText(message.timestamp, previousTimestamp)

                ChatBubble(
                    message = message.text,
                    time = formatTime(message.timestamp),
                    isSent = message.senderId == currentUserId,
                    middleTimeText = middleTimeText
                )
            }
        }
    }
}




@Composable
fun ChatBubble(
    message: String,
    time: String,
    isSent: Boolean,
    middleTimeText: String? // Mốc giữa
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (isSent) Alignment.End else Alignment.Start
    ) {
        // ✅ Mốc thời gian ở giữa
        middleTimeText?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        // ✅ Tin nhắn + thời gian nhỏ
        Column(horizontalAlignment = if (isSent) Alignment.End else Alignment.Start) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSent) Color.Blue else Color.Gray
                ),
                modifier = Modifier
                    .padding(4.dp)
                    .widthIn(max = 220.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = message, color = Color.White)
                    Text(
                        text = time,
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun FrameChatting(chatId: String, currentUserId: String) {

    var text by remember { mutableStateOf("") }
    var showEmojiPicker by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painterResource(id = R.drawable.mepmanhinhduoi),
            contentDescription = "màn hình dưới",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            // Hiển thị emoji picker khi được kích hoạt
            AnimatedVisibility(
                visible = showEmojiPicker,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                EmojiPicker(
                    onEmojiSelected = { emoji ->
                        text += emoji
                    },
                    onDismiss = {
                        showEmojiPicker = false
                    }
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 15.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "add circle",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
                Box(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { Text("Nhập tin nhắn...") },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(25.dp))
                    )

                    Image(
                        painterResource(id = R.drawable.insert_emoticon),
                        contentDescription = "icon",
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .size(30.dp)
                            .align(Alignment.CenterEnd)
                            .clickable {
                                showEmojiPicker = !showEmojiPicker
                            }
                    )
                }

                Image(
                    painterResource(id = R.drawable.send),
                    contentDescription = "nút like =))",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color(0xFFFFFFFF)),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 30.dp, bottom = 10.dp)
                        .width(37.dp)
                        .height(34.dp)
                        .clickable {
                            if (text.isNotBlank()) {
                                Chatting.sendMessage(chatId, currentUserId, text)
                                text = "" // Xóa nội dung sau khi gửi
                            }
                        }
                )
            }
        }
    }
}

fun getMiddleTimestampText(current: Long, previous: Long?): String? {
    if (previous == null) return formatFullDate(current)

    val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(current))
    val prevDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(previous))

    return when {
        currentDate != prevDate -> formatFullDate(current)
        current - previous >= 5 * 60 * 1000 -> formatTime(current)
        else -> null
    }
}

fun formatTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

fun formatFullDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
