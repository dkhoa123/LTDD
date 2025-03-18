package com.example.appchat_tbdd.ui_layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appchat_tbdd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun signInLayout(navController: NavController? = null, modifier: Modifier = Modifier){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // Kiểm soát hiển thị mật khẩu
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCAF0F8)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(4.dp)
        ) {
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 260f,
                center = center.copy(x = size.width * 0.20f, y = size.height * 0.05f)
            )
            drawCircle(
                color = Color(0xFF0096C7),
                radius = 400f,
                center = center.copy(x = size.width * 0.7f, y = size.height * 0.05f)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 260f,
                center = center.copy(x = size.width * 0.1f, y = size.height * 0.95f)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 100f,
                center = center.copy(x = size.width * 0.4f, y = size.height * 0.85f)
            )
        }
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row (
                modifier = Modifier
                    .padding(end = 150.dp, bottom = 10.dp)
            ){
                Text(
                    text = "Sign In",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(end = 25.dp)
                )
                Text(
                    text = "Sign Up",
                    fontSize = 24.sp,
                    color = Color.Gray,
                )
            }
            // Ô nhập Email hoặc Số điện thoại
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Phone/ Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp), // Bo góc
                //Jetpack Compose Material3
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ô nhập Mật khẩu
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = "Toggle Password Visibility")
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                //Jetpack Compose Material3
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row (
                    modifier = Modifier.padding(end = 75.dp)
                ){
                    Image(
                        painter = painterResource(id = R.drawable.icon_check_circle),
                        contentDescription = "Checkbox",
                        modifier = Modifier
                            .padding(end = 4.dp)
                    )

                    Text(
                        text = "Remember me",
                        fontSize = 16.sp,
                    )
                }

                Text(
                    text = "Forgot password",
                    fontSize = 16.sp,
                    color = Color(0xFF03045E)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Nút Đăng nhập
            Button(
                onClick = { /* Xử lý đăng nhập */ },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF023E8A)) // Màu xanh dương đậm
            ) {
                Text(text = "Sign In", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewSignInLayout (){
    signInLayout()
}