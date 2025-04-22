package com.example.uiappchat.ui.theme.UI

import PreCameraScreen
import android.net.Uri
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
import com.example.app_chat_new.components.BottomIconBar
import com.example.uiappchat.ViewModel.AuthViewModel
import com.example.uiappchat.R
import com.example.uiappchat.ViewModel.handleCapture
import com.example.uiappchat.ViewModel.toggleCamera
import com.example.uiappchat.ViewModel.toggleFlash
import com.example.uiappchat.ui.theme.components.TopAppBarCommon
import java.net.URLEncoder

object CameraState {
//    val isFrontCamera = mutableStateOf(false) // true: front camera, false: back camera
//    val flashMode = mutableStateOf(ImageCapture.FLASH_MODE_OFF) // Flash mode
    val lastCapturedImageUri = mutableStateOf<Uri?>(null) // URI của ảnh vừa chụp
//    var imageCapture: ImageCapture? = null // Instance của ImageCapture
}
@Composable
fun CameraScreen(navController: NavHostController, authViewModel: AuthViewModel = viewModel()
) {
    val userId by authViewModel.userId.collectAsState()
    val context = LocalContext.current
//    val isFrontCamera by CameraState.isFrontCamera
//    val flashMode by CameraState.flashMode
    val lastCapturedImageUri by CameraState.lastCapturedImageUri

    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri
        val encodedUri = URLEncoder.encode(uri.toString(), "UTF-8")
        navController.navigate("postimagescreen?userId=$userId&imageUri=$encodedUri")
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

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 80.dp), // Đẩy cụm Camera và các nút xuống giữa
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color.White)
            ) {
                PreCameraScreen()
            }

            Spacer(modifier = Modifier.height(20.dp)) // Giãn cách camera và các nút

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_3),
                    contentDescription = "Flash",
                    modifier = Modifier.size(30.dp).clickable { toggleFlash() }
                )
                Image(
                    painter = painterResource(id = R.drawable.img_2),
                    contentDescription = "Capture",
                    modifier = Modifier.size(80.dp).clickable {
                        handleCapture(context) { uri ->
                        CameraState.lastCapturedImageUri.value = uri

                        val encodedUri = URLEncoder.encode(uri.toString(), "UTF-8")
                        navController.navigate("postimagescreen?userId=$userId&imageUri=$encodedUri")
                        }
                    }
                )
                Image(
                    painter = painterResource(id = R.drawable.img_4),
                    contentDescription = "Switch Camera",
                    modifier = Modifier.size(30.dp).clickable { toggleCamera() }
                )
            }

            Spacer(modifier = Modifier.height(5.dp)) // Giãn cách trước nút chuyển sang post

            Image(
                painter = painterResource(id = R.drawable.img_5),
                contentDescription = "Navigate to Post",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clickable {
                        lastCapturedImageUri?.let { uri ->
                            val encodedUri = URLEncoder.encode(uri.toString(), "UTF-8")
                            navController.navigate("postimagescreen?userId=$userId&imageUri=$encodedUri")
                        } ?: Toast.makeText(context, "Vui lòng chụp ảnh trước", Toast.LENGTH_SHORT).show()
                    }
            )
        }

        userId?.let {
            BottomIconBar(
                navController = navController,
                userId = it,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp)
            )
        }

    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    val fakeNavController = rememberNavController()
//    val userId = "PLBVPAdhfzXKqxqmK8ZO3baly143";
//    UIAppChatTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
//        }
//        CameraScreen(navController = fakeNavController, userId = userId)
//    }
//}
