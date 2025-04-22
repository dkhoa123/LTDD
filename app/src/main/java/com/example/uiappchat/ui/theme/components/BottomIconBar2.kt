package com.example.app_chat_new.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import com.example.uiappchat.R

@Composable
fun BottomIconBar2(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userId: String
) {
    var selectedIcon by remember { mutableStateOf(R.drawable.img_8) }

    Box(
        modifier = modifier
            .fillMaxWidth()
//            .height(150.dp)
    ) {
        Image(
            painterResource(id = R.drawable.ellipse),
            contentDescription = "taskbars",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(
                    horizontal = 60.dp)
        ) {
            // Icon trái (SimpleScreenLayout)
            IconButton(
                onClick = {
                    selectedIcon = R.drawable.img_6
                    navController.navigate("screen2?=userId=$userId")
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_6),
                    contentDescription = "Left Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .alpha(if (selectedIcon == R.drawable.img_6) 1f else 0.7f)
                        .shadow(if (selectedIcon == R.drawable.img_6) 8.dp else 0.dp, CircleShape)
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            // Icon giữa (CustomScreen)
            IconButton(
                onClick = {
                    selectedIcon = R.drawable.img_8
                    navController.navigate("camerascreen?userId=$userId")
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_7),
                    contentDescription = "Middle Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .alpha(if (selectedIcon == R.drawable.img_7) 1f else 0.7f)
                        .shadow(if (selectedIcon == R.drawable.img_7) 8.dp else 0.dp, CircleShape)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // Icon phải (CustomScreen2)
            IconButton(
                onClick = {
                    selectedIcon = R.drawable.img_8
                    navController.navigate("profilescreen?userId=$userId")
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_8),
                    contentDescription = "Right Icon",
                    modifier = Modifier
                        .size(45.dp)
                        .alpha(if (selectedIcon == R.drawable.img_8) 1f else 0.7f)
                        .shadow(if (selectedIcon == R.drawable.img_8) 8.dp else 0.dp, CircleShape)
                )
            }
        }
    }
}