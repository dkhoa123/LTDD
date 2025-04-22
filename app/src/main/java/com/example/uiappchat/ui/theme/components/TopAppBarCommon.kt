package com.example.uiappchat.ui.theme.components

import com.example.uiappchat.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.example.uiappchat.ui.theme.BluePrimary

// components/TopAppBarCommon.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarCommon(
    navController: NavController,
    userId: String,
    showBackIcon: Boolean = true,
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    modifier: Modifier
) {
    TopAppBar(
        modifier = Modifier.height(100.dp).padding(top = 20.dp),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = {
                    navController.navigate("screen2?userId=$userId")
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    )
}