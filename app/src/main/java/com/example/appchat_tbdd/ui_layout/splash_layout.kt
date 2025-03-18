package com.example.appchat_tbdd.ui_layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appchat_tbdd.R


@Composable
fun Splash_layout (navController: NavController? = null, modifier: Modifier = Modifier){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAF0F8)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .blur(4.dp) // Độ mờ (có thể điều chỉnh)
        ) {
            drawCircle(
                color = Color(0xFF0096C7), // Màu xanh đậm
                radius = 250f,
                center = center.copy(x = size.width * 0.45f, y = size.height * 0)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 175f,
                center = center.copy(x = size.width * 0.3f, y = size.height * 0.1f)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 250f,
                center = center.copy(x = size.width * 1f, y = size.height * 0.6f)
            )
            drawCircle(
                color = Color(0xFF0096C7),
                radius = 175f,
                center = center.copy(x = size.width * 1f, y = size.height * 0.5f)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 250f,
                center = center.copy(x = size.width * 0, y = size.height * 0.7f)
            )
            drawCircle(
                color = Color(0xFF0096C7),
                radius = 175f,
                center = center.copy(x = size.width * 0.1f, y = size.height * 0.83f)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 175f,
                center = center.copy(x = size.width * 0.6f, y = size.height * 0.928f)
            )
        }
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_app_chat),
                contentDescription = "Logo",
                modifier = Modifier.size(350.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.image_demo_super_private),
                contentDescription = "Demo Super Private",
                modifier = Modifier.size(100.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun Splash_layout_preview (){
    Splash_layout()
}