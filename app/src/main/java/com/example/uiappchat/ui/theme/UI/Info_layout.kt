package com.example.uiappchat.ui.theme.UI

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uiappchat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Info_layout(navController: NavController) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf("Nam") }
    var email by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }


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
                color = Color(0xFF0096C7),
                radius = 260f,
                center = center.copy(x = size.width * 0.8f, y = size.height * 0.05f)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 500f,
                center = center.copy(x = size.width * 0.2f, y = size.height * 0)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 260f,
                center = center.copy(x = size.width * 0.9f, y = size.height * 0.95f)
            )
            drawCircle(
                color = Color(0xFF0077B6),
                radius = 100f,
                center = center.copy(x = size.width * 0.25f, y = size.height * 0.87f)
            )
        }

        if (errorMessage != null) {
            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_LONG).show()
            errorMessage = null
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 150.dp, end = 150.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 24.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(end = 25.dp)
                        .clickable { navController.navigate("home") }
                )
                Text(
                    text = "Sign Up",
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Sex", fontSize = 16.sp, fontWeight = FontWeight.Medium)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    RadioButton(
                        selected = sex == "Nam",
                        onClick = { sex = "Nam" },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF023E8A))
                    )
                    Text(text = "Nam", modifier = Modifier.padding(end = 24.dp))

                    RadioButton(
                        selected = sex == "Nữ",
                        onClick = { sex = "Nữ" },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF023E8A))
                    )
                    Text(text = "Nữ")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Validate required fields
                    if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
                        errorMessage = "Please fill in all required fields"
                        return@Button
                    }
                    // Validate email format
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        errorMessage = "Invalid email format"
                        return@Button
                    }
                    // Store user details temporarily (you might want to use a ViewModel for this)
                    // For now, just pass the email to the next screen
                    navController.navigate("screen6?email=$email&firstName=$firstName&lastName=$lastName&age=$age&address=$address&sex=$sex")
                },
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(Color.Gray),
                shape = RoundedCornerShape(25.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image_button_next),
                    contentDescription = "Next",
                    modifier = Modifier
                        .size(50.dp)
                        .fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}