package com.example.uiappchat.ui.theme.UI

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_chat_new.components.BottomIconBar
import com.example.uiappchat.ViewModel.AuthViewModel
import com.example.uiappchat.R
import com.example.uiappchat.ui.theme.components.TopAppBarCommon

@Composable
fun PostImageScreen(
    navController: NavHostController,
    imageUriString: String?,
    authViewModel: AuthViewModel = viewModel()
) {

    val userId by authViewModel.userId.collectAsState()
    val uriString = navController.currentBackStackEntry?.arguments?.getString("imageUri")
    val imageUri = uriString?.let { Uri.parse(it) }

    val selectedImageUri = remember { mutableStateOf(imageUriString?.let { Uri.parse(it) }) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri
    }


    // Đảm bảo rằng imageUri không phải là null

    imageUri?.let {
        // Xử lý ảnh đã nhận
        Log.d("PostImageScreen", "Đã nhận ảnh: $it")
    } ?: run {
        Toast.makeText(LocalContext.current, "Không có ảnh để hiển thị", Toast.LENGTH_SHORT).show()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1C2833))) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "Top Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        userId?.let {
            TopAppBarCommon(
                navController = navController,
                userId = it,
                onBackClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp, start = 30.dp, end = 30.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_9),
                contentDescription = "Left Icon",
                modifier = Modifier.size(30.dp).clickable { navController.navigate("setting_screen") }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("20", fontSize = 17.sp, color = Color.White)
                Text(" posts", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red, CircleShape)
                        .offset(x = 4.dp, y = (-8).dp)
                )
            }
            Image(
                painter = painterResource(id = R.drawable.img_10),
                contentDescription = "Right Icon",
                modifier = Modifier.size(30.dp)
                    .clickable {
                        imagePickerLauncher.launch("image/*") // Mở thư viện ảnh
                    }
            )
        }

        // Column chứa hộp trắng và các thành phần bên dưới nó
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 80.dp), // Đảm bảo không bị đè lên BottomIconBar
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(200.dp)) // Khoảng cách từ hàng icon/text xuống hộp trắng

            // Hộp trắng ở giữa màn hình
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                selectedImageUri.value?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Ảnh đã chọn",
                        modifier = Modifier.fillMaxSize(), // hoặc scale phù hợp
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Text(text = "Chưa chọn ảnh", color = Color.Gray, fontSize = 14.sp)
                }
            }


            Spacer(modifier = Modifier.height(20.dp)) // Giảm khoảng cách để bố cục chặt chẽ hơn

            // Hàng chứa avatar, tên, thời gian
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                // Avatar
                Image(
                    painter = painterResource(id = R.drawable.img_15), // Avatar hình tròn
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.width(8.dp)) // Khoảng cách

                // Tên + Thời gian
                Text(
                    text = "Anh",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "8h",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp)) // Khoảng cách

            // Hàng chứa các icon cảm xúc
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(0.6f) // Thu nhỏ lại 60% chiều rộng
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_11),
                    contentDescription = "Heart",
                    modifier = Modifier.size(24.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.img_12),
                    contentDescription = "Smile",
                    modifier = Modifier.size(24.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.img_13),
                    contentDescription = "Sad",
                    modifier = Modifier.size(24.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.img_14),
                    contentDescription = "Add Smile",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp)) // Khoảng cách

            // Thanh nhập bình luận
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Thu nhỏ 80% chiều rộng màn hình
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFD1D1D1)), // Màu xám nhạt
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Aa",
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp)) // Khoảng cách giữa thanh nhập bình luận và nút xoay camera

            // Nút Xoay Camera
            Image(
                painter = painterResource(id = R.drawable.img_5),
                contentDescription = "Switch Camera",
                modifier = Modifier
                    .size(60.dp)
                    .clickable { /* Xử lý đổi camera trước/sau */ }
            )
        }

        // Box chứa img_1 và hàng icon
        userId?.let {
            BottomIconBar(
                navController = navController,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp), // Bỏ offset và đặt cố định dưới cùng
                userId = it
            )
        }
    }
}