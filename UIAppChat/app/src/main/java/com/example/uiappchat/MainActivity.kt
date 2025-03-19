package com.example.uiappchat

import android.os.Bundle
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.uiappchat.ui.theme.UIAppChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UIAppChatTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
//                  ListFriend()
//                  BoxChat()
//                  ContactsScreen3()
                    Change3Screen()
                }
            }
        }
    }
}
@Composable
fun Change3Screen(){
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = "home"){
        composable("home"){ ListFriendScreen1(navController) }
        composable("screen2"){ BoxChatScreen2(navController) }
        composable("screen3"){ ContactsScreen3(navController) }
    }
}
@Composable
fun ListFriendScreen1(navController: NavController){
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1B263B)))
    {
            AddandSearch(navController)
        Box(
            modifier = Modifier
                .weight(1f) // Giới hạn chiều cao để không đè lên TaskBar
                .fillMaxWidth()
        ) {
            MessageList(navController)
        }
        TaskBars()
    }
}
@Composable
fun AddandSearch(navController: NavController){
    Box( modifier = Modifier.fillMaxWidth()) {
        Image(painterResource(id = R.drawable.mepmanhinhtrai),
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
                    .height(27.dp),
                tint = Color(0xFFFFFFFF)
            )
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .width(27.dp)
                    .height(27.dp),
                tint = Color(0xFFFFFFFF)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()
            .padding(top = 120.dp, start = 45.dp, end = 45.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Friends",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFCFBFE)
            )
            Text(text = "Contacts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF).copy(alpha = 0.25f),
                modifier = Modifier.clickable { navController.navigate("screen3") }
            )
        }
    }
}
data class MessageData(
    val name: String,
    val content: String,
    val time: String,
    val unreadCount: Int
)
@Composable
fun MessageList(navController: NavController) {
    val messages = listOf(
        MessageData("Me", "I love you", "15/3/2025", 8)

    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(messages) { message ->
            List(message, navController)
        }
    }
}
@Composable
fun List(message: MessageData, navController: NavController){
    Box(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
        Box( modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0077B6))
                    .clickable { navController.navigate("screen2") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(id = R.drawable.sadboy),
                    contentDescription = "ảnh",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(45.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { navController.navigate("screen2") }
                )
                Column {
                    Text(
                        text = "Me",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .padding(end = 16.dp) // Cách mép trên & phải
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "I love you", color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.fillMaxHeight().padding(end = 10.dp),
                ) {
                    Text(
                        text = "15/3/2025",
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
                            .background(Color(0xFF00B912)) // Màu xanh trạng thái
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Red)
                .align(Alignment.TopEnd) // Đẩy badge lên góc phải
        ) {
            Text(
                text = "8",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
@Composable
fun TaskBars() {
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
            Image(painterResource(id = R.drawable.chat),
                contentDescription = "chat",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color(0xFFFFFFFF))
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(painterResource(id = R.drawable.camera),
                contentDescription = "chat",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color(0x8000BCD4))
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(painterResource(id = R.drawable.person),
                contentDescription = "chat",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color(0x8000BCD4))
            )
        }
    }
}

@Composable
fun BoxChatScreen2(navController: NavController){
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1B263B))
    ) {
        TopName(navController)
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth())
        {
            ChatScreen()
        }
        FrameChatting()
    }
}
@Composable
fun TopName(navController: NavController){
    Box(modifier = Modifier.fillMaxWidth()){
        Image(painterResource(id = R.drawable.mepmanhinhfullbentren),
            contentDescription ="TopName",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth()
            .padding(top = 50.dp, start = 10.dp,end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
            Icon(imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(30.dp)
                    .clickable { navController.navigate("home") },
                tint = Color.White)
            Column(modifier = Modifier.weight(1f).padding(start = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Me",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White)
                Text(text = "Online",
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color.White)
            }
            Icon(imageVector = Icons.Default.Call,
                contentDescription = "Call",
                modifier = Modifier.size(32.dp),
                tint = Color.White)
            Image(painterResource(id =R.drawable.videocam),
                contentDescription = "videocam",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color.White))
        }
    }
}

@Composable
fun ChatScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(8.dp),
//        reverseLayout = true // Để tin nhắn mới nhất xuất hiện ở dưới cùng
    ) {
        items(chatMessages) { (message, time, isSent) ->
            ChatBubble(message, time, isSent)
        }
    }
}

// Danh sách tin nhắn mẫu
val chatMessages = listOf(
    Triple("Hello!", "12:30", false),
    Triple("Hi there!", "2:00", true),
    Triple("How are you?", "6:33", false),
    Triple("I'm good, thanks!", "7:45", true)
)



@Composable
fun ChatBubble(message: String, time: String, isSent: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (isSent) Alignment.End else Alignment.Start
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSent) Color.Blue else Color.Gray
            ),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
        // Hiển thị thời gian bên dưới tin nhắn
        Text(
            text = time,
            fontSize = 12.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
        )
    }
    Text(
        text = time,
        fontSize = 12.sp,
        color = Color.LightGray,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}
@Composable
fun FrameChatting(){
    var text by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxWidth()){
        Image(painterResource(id = R.drawable.mepmanhinhduoi),
            contentDescription = "màn hình dưới",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter))
        Row(modifier = Modifier.fillMaxWidth()
            .align(Alignment.Center)
            .padding(start = 5.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "add circle",
                modifier = Modifier.size (30.dp),
                tint = Color.White)
            Box() {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Nhập tin nhắn...") },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(25.dp))
                )
                Image(painterResource(id=R.drawable.insert_emoticon),
                    contentDescription = "icon",
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(30.dp)
                        .align(Alignment.CenterEnd)
                        )
            }
            Image(painterResource(id = R.drawable.thumb_up),
                contentDescription = "nút like =))",
                modifier = Modifier
                    .padding(start = 20.dp,end = 5.dp, bottom = 10.dp)
                    .width(37.dp)
                    .height(34.dp)

            )
        }
    }
}

@Composable
fun ContactsScreen3(navController: NavController){
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1B263B)))
    {
        AddandSearchScreen3(navController)

        Box(
            modifier = Modifier
                .weight(1f) // Giới hạn chiều cao để không đè lên TaskBar
                .fillMaxWidth()
        ) {
            ListAddFriend()
        }
        TaskBarsScreen3()
    }
}

@Composable
fun AddandSearchScreen3(navController: NavController){
    Box( modifier = Modifier.fillMaxWidth()) {
        Image(painterResource(id = R.drawable.mepmanhinhphai),
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
                    .height(27.dp),
                tint = Color(0xFFFFFFFF)
            )
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .width(27.dp)
                    .height(27.dp),
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
                modifier = Modifier.clickable { navController.navigate("home") }
            )
            Text(text = "Contacts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFCFBFE)
            )
        }
    }
}


data class ContactGroup(val letter: String, val names: List<String>)
val contacts = listOf(
    ContactGroup("A", listOf("Antony", "Alex", "Anh")),
    ContactGroup("B", listOf("Ba", "Box", "BigDaddy", "Baby")),
    ContactGroup("C", listOf("CucCung"))
)
@Composable
fun ContactItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
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
fun ListAddFriend() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                Text(
                    text = "All 315",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .background(Color(0xFFD9D9D9), shape = RoundedCornerShape(22.dp))
                        .padding(8.dp)
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                contacts.forEach { group ->
                    item {
                        Text(
                            text = group.letter,
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(group.names) { name ->
                        ContactItem(name)
                    }
                }
            }
        }
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
fun TaskBarsScreen3() {
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
            Image(painterResource(id = R.drawable.chat),
                contentDescription = "chat",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color(0xFFFFFFFF))
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(painterResource(id = R.drawable.camera),
                contentDescription = "chat",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color(0x8000BCD4))
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(painterResource(id = R.drawable.person),
                contentDescription = "chat",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(Color(0x8000BCD4))
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val fakeNavController = rememberNavController()
    UIAppChatTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
//              ListFriend()
            BoxChatScreen2(navController = fakeNavController)
//            ContactsScreen3()
//            Change3Screen()
        }
    }
}