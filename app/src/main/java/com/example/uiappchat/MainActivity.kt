@file:Suppress("NAME_SHADOWING")

package com.example.uiappchat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uiappchat.Model.UserPreferences
import com.example.uiappchat.ViewModel.AuthViewModel
import com.example.uiappchat.ViewModel.ProfileViewModel
import com.example.uiappchat.ViewModel.SignInViewModel
import com.example.uiappchat.ui.theme.UIAppChatTheme
import com.example.uiappchat.ui.theme.UI.BoxChatScreen2
import com.example.uiappchat.ui.theme.UI.CameraScreen
import com.example.uiappchat.ui.theme.UI.ContactsScreen3
import com.example.uiappchat.ui.theme.UI.Info_layout
import com.example.uiappchat.ui.theme.UI.ListFriendScreen1
import com.example.uiappchat.ui.theme.UI.PostImageScreen
import com.example.uiappchat.ui.theme.UI.ProfileScreen
import com.example.uiappchat.ui.theme.UI.SignInLayout
import com.example.uiappchat.ui.theme.UI.Sign_up_layout
import com.example.uiappchat.ui.theme.UI.Suggestions
import androidx.lifecycle.viewmodel.compose.viewModel as viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            UIAppChatTheme {
                Surface (modifier = Modifier.fillMaxSize()
                    .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
                    Change3Screen()
                }
            }
        }
    }
}


@SuppressLint("RestrictedApi")
@Composable
fun Change3Screen(
    authViewModel: AuthViewModel = viewModel()
) {
    val viewModel = viewModel<SignInViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    val navController = rememberNavController()
    val userId by authViewModel.userId.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val rememberMe by userPrefs.rememberMeFlow.collectAsState(initial = false)
    val savedUserId by userPrefs.userIdFlow.collectAsState(initial = null)

    LaunchedEffect(rememberMe, savedUserId) {
        if (rememberMe && savedUserId != null) {
            navController.navigate("screen2?userId=$savedUserId") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(uiState.userId) {
        uiState.userId?.let { userId ->
            navController.navigate("screen2?userId=$userId") {
                popUpTo("home") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // Log back stack để debug
    LaunchedEffect(Unit) {
        Log.d("Navigation", "Change3Screen: Back stack ban đầu: ${navController.currentBackStack.value.map { it.destination.route }}")
    }

    // Điều hướng về home nếu không đăng nhập
    LaunchedEffect(isLoggedIn, userId) {
        if (!isLoggedIn || userId == null) {
            Log.d("Navigation", "Change3Screen: Không đăng nhập hoặc userId null, chuyển về home")
            navController.navigate("home") {
                popUpTo("home") { inclusive = true } // Sử dụng route "home"
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            val signInViewModel: SignInViewModel = viewModel()
            SignInLayout(navController, signInViewModel)
        }

        composable("screen2") {
            ListFriendScreen1(navController, authViewModel)
        }

        composable("screen3?chatId={chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            if (isLoggedIn && userId != null && chatId.isNotEmpty()) {
                BoxChatScreen2(navController, chatId, userId!!)
            } else {
                Log.d("Navigation", "screen3: userId hoặc chatId không hợp lệ, chuyển về home")
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable("screen4") {
            if (isLoggedIn && userId != null) {
                ContactsScreen3(navController, authViewModel)
            } else {
                Log.d("Navigation", "screen4: userId không hợp lệ, chuyển về home")
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable("screen5") {
            Info_layout(navController)
        }

        composable(
            "screen6?email={email}&firstName={firstName}&lastName={lastName}&age={age}&address={address}&sex={sex}&imageUri={imageUri}"
        ) { backStackEntry ->
            val emailfromInfo = backStackEntry.arguments?.getString("email") ?: ""
            val firstName = backStackEntry.arguments?.getString("firstName") ?: ""
            val lastName = backStackEntry.arguments?.getString("lastName") ?: ""
            val age = backStackEntry.arguments?.getString("age") ?: ""
            val address = backStackEntry.arguments?.getString("address") ?: ""
            val sex = backStackEntry.arguments?.getString("sex") ?: ""

            Sign_up_layout(
                navController,
                emailfromInfo,
                firstName,
                lastName,
                age,
                address,
                sex
            )
        }

        composable("screen7") {
            if (isLoggedIn && userId != null) {
                Suggestions(navController, authViewModel)
            } else {
                Log.d("Navigation", "screen7: userId không hợp lệ, chuyển về home")
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable("camerascreen") {
            if (isLoggedIn && userId != null) {
                CameraScreen(navController, authViewModel)
            } else {
                Log.d("Navigation", "camerascreen: userId không hợp lệ, chuyển về home")
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable("postimagescreen?userId={userId}&imageUri={imageUri}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val imageUriString = backStackEntry.arguments?.getString("imageUri")

            if (!userId.isNullOrBlank()) {
                PostImageScreen(
                    navController = navController,
                    imageUriString = imageUriString
                )
            } else {
                Log.d("Navigation", "postimagescreen: userId không hợp lệ, chuyển về home")
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }


        composable("profilescreen") {            if (isLoggedIn && userId != null) {
                val profileViewModel: ProfileViewModel = viewModel()
                ProfileScreen(navController, profileViewModel, authViewModel)
            } else {
                Log.d("Navigation", "profilescreen: userId không hợp lệ, chuyển về home")
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    val fakeNavController = rememberNavController()
//    UIAppChatTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
////              ListFriend()
//        }
//    }
//}