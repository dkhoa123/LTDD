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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.uiappchat.Model.InfoUser
import com.example.uiappchat.ViewModel.addFriend
import com.example.uiappchat.ViewModel.addFriend.getFriendsListFromRealtimeDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ContactsScreen3(navController: NavController,
                    authViewModel: AuthViewModel = viewModel()
){
    val userId by authViewModel.userId.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1B263B)))
    {
        userId?.let { AddandSearchScreen3(navController, it) }

        Box(
            modifier = Modifier
                .weight(1f) // Giới hạn chiều cao để không đè lên TaskBar
                .fillMaxWidth()
        ) {
            userId?.let { ListAddFriend(navController, it) }
        }
        userId?.let { TaskBarsScreen3(navController, it) }
    }
}

@Composable
fun AddandSearchScreen3(navController: NavController, userId: String){
    var isSearching by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    Box( modifier = Modifier.fillMaxWidth()) {
        Image(
            painterResource(id = R.drawable.mepmanhinhphai),
            contentDescription = "mép trái",
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
                                if (user != null && user.userId != null) {
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
                color =  Color(0xFFFFFFFF).copy(alpha = 0.25f),
                modifier = Modifier.clickable { navController.navigate("screen2?userId=$userId") }
            )
            Text(text = "Contacts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFCFBFE),
                modifier = Modifier.clickable { navController.navigate("screen4?userId=$userId") }
            )
        }
    }
}


@Composable
fun ContactItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(id=R.drawable.khunglong),
            contentDescription = "ảnh đại diện",
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
        ) // Ô vuông màu trắng

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = name,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}
@Composable
fun ListAddFriend(navController: NavController, userId: String) {
    var friends by remember { mutableStateOf<List<InfoUser>>(emptyList()) }
    var friendRequestsCount by remember { mutableStateOf(0) }

    // Lấy số lượng lời mời kết bạn từ Firebase
    LaunchedEffect(userId) {
        val friendRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests").child(userId)
        friendRequestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friendRequestsCount = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi khi lấy số lượng lời mời kết bạn: ${error.message}")
            }
        })
    }

    // Lấy danh sách bạn bè từ Firebase
    LaunchedEffect(userId) {
        getFriendsListFromRealtimeDB(userId) { friends = it }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                Text(
                    text = "All ${friends.size}", // Sử dụng friends.size để hiển thị số lượng
                    fontSize = 20.sp,
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(22.dp))
                        .padding(8.dp)
                        .clickable { navController.navigate("screen4") }
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Hiển thị "Suggestions"
                Box {
                    Text(
                        text = "Suggestions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(22.dp))
                            .padding(8.dp)
                            .clickable { navController.navigate("screen7") }
                    )
                    // Hiển thị notification badge khi có lời mời kết bạn
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(friends) { friend ->
                    ContactItem("${friend.firstName} ${friend.lastName}")
                }
            }
        }

        // Phần hiển thị chữ cái alphabet
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val letters = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N")
            letters.forEach { letter ->
                Text(
                    text = letter,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun TaskBarsScreen3(navController: NavController, userId: String) {
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
                modifier = Modifier.size(40.dp)
                    .clickable { navController.navigate("screen2?userId=$userId") },
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
                    .clickable { navController.navigate("profilescreen?=userId=$userId") },
                colorFilter = ColorFilter.tint(Color(0x8000BCD4))
            )
        }
    }
}