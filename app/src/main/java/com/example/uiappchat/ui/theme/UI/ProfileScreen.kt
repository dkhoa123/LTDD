package com.example.uiappchat.ui.theme.UI


import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app_chat_new.components.BottomIconBar2
import com.example.uiappchat.Model.UserPreferences
import com.example.uiappchat.R
import com.example.uiappchat.ViewModel.AuthViewModel
import com.example.uiappchat.ViewModel.ProfileViewModel
import com.example.uiappchat.ui.theme.components.TopAppBarCommon
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("RestrictedApi")
@Composable
fun ProfileScreen(navController: NavHostController,
                  viewModel: ProfileViewModel,
                  authViewModel: AuthViewModel
) {
    val userId by authViewModel.userId.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()
    val photoUrl by viewModel.photoUrl.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    // Xử lý điều hướng khi đăng xuất thành công
    LaunchedEffect(uiState.isSignedOut) {
        if (uiState.isSignedOut) {
            Log.d("Navigation", "Back stack trước khi đăng xuất: ${navController.currentBackStack.value.map { it.destination.route }}")
            authViewModel.refreshAuthState() // Cập nhật trạng thái đăng nhập
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
            Log.d("Navigation", "Back stack sau khi đăng xuất: ${navController.currentBackStack.value.map { it.destination.route }}")
        }
    }

    // Lấy thông tin người dùng khi vào màn hình
    LaunchedEffect(userId) {
        userId?.let { viewModel.fetchUserInfo(it) }
    }
    Scaffold(
        topBar = {
            userId?.let {
                TopAppBarCommon(
                    navController = navController,
                    userId = it,
                    onBackClick = { navController.popBackStack() },
                    modifier = Modifier
                )
            }
        },
        bottomBar = {
            userId?.let {
                BottomIconBar2(
                    navController = navController,
                    userId = it,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    // Không đặt bottom padding ở đây, sẽ xử lý trong phần cuộn
                    bottom = 0.dp)
        ) {
            // Khu vực nền xanh nhạt
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFB3E5FC))
            ) {
                if(photoUrl == null) {
                    Image(
                        painterResource(id = R.drawable.background_appchat),
                        contentDescription = "Imageprofile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                } else {
                    AsyncImage(
                        model = photoUrl ?: "default_image_url",
                        contentDescription = "Imageprofile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            // Khu vực nền xanh đậm - kéo dài đến tận cùng màn hình
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 200.dp)
                    .background(Color(0xFF1C2833))
            )

            // Avatar + Tiêu đề + Danh sách bài post
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hình tròn Avatar
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF004C8C))
                ) {
                    if(photoUrl == null) {
                        Image(
                            painterResource(id = R.drawable.img_8),
                            contentDescription = "Imageprofile",
                            modifier = Modifier
                                .size(125.dp)
                                .clip(RoundedCornerShape(125.dp))
                                .align(Alignment.Center)
                        )
                    } else {
                        AsyncImage(
                            model = photoUrl ?: "default_image_url",
                            contentDescription = "Imageprofile",
                            modifier = Modifier
                                .size(125.dp)
                                .clip(RoundedCornerShape(125.dp))
                                .align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tiêu đề tên người dùng
                Text(
                    text = userInfo?.let { "${it.firstName} ${it.lastName}" } ?: "Đang tải...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Phần cuộn chứa thông tin cá nhân
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                        // Thêm padding bottom đúng với kích thước của bottom bar
                        .padding(bottom = paddingValues.calculateBottomPadding() + 8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Email Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Email",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = userInfo?.email ?: "Email",
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    text = "Email",
                                    style = TextStyle(Color.White)
                                )
                            },
                            textStyle = TextStyle(Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Address Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Address",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = userInfo?.address ?: "Loading",
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    text = "Address",
                                    style = TextStyle(Color.White)
                                )
                            },
                            textStyle = TextStyle(Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Sex Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Sex",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = userInfo?.sex ?: "Loading",
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    text = "Sex",
                                    style = TextStyle(Color.White)
                                )
                            },
                            textStyle = TextStyle(Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Sign Out Button
                    Button(
                        modifier = Modifier
                            .padding(top = 24.dp),
                        onClick = {
                            viewModel.signOut(context, authViewModel)
                            val userPreferences = UserPreferences(context)
                            kotlinx.coroutines.GlobalScope.launch {
                                userPreferences.clearUserData()
                            }
                        },
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(text = "Sign Out")
                        }
                    }
                    // Spacer để tránh bị che bởi BottomBar
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }

            // Hiển thị lỗi nếu có
            uiState.errorMessage?.let { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

