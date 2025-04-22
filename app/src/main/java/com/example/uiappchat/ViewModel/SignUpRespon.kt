package com.example.uiappchat.ViewModel

import android.content.Context
import androidx.navigation.NavController
import com.example.uiappchat.Model.InfoUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


object SignUpRespon {

    // Save user info to both Firebase Realtime Database and Firestore
    fun saveUserInfo(user: InfoUser, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Step 1: Save to Realtime Database
        val dbRealtime = FirebaseDatabase.getInstance().reference
        val userMap = mapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "email" to user.email,
            "age" to user.age,
            "address" to user.address,
            "sex" to user.sex
        )

        // Save to Realtime Database under users/USER_ID
        dbRealtime.child("users").child(userId).setValue(userMap)
            .addOnSuccessListener {
                // Step 2: Save to Firestore after Realtime Database succeeds
                val dbFirestore = FirebaseFirestore.getInstance()
                dbFirestore.collection("users").document(user.email) // Using email as the document ID
                    .set(user)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFailure(e) }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Đăng ký tài khoản với Firebase Authentication
    fun signUpWithEmail(email: String, password: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    onSuccess(userId)
                } else {
                    onFailure(Exception("Không lấy được UID"))
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun signOut(context: Context, navController: NavController, viewModel: ProfileViewModel) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser?.uid

        // Đặt trạng thái offline nếu người dùng đang đăng nhập
        if (currentUserId != null) {
            FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("online")
                .setValue(false)
        }

        // Đăng xuất Firebase
        firebaseAuth.signOut()

        // Đăng xuất Google (nếu dùng Google Sign-In)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("656943469501-d3m240a31t375aih1lk04dovra457gvm.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess().addOnCompleteListener {
                // Xóa ViewModel
                viewModel.clear()

                // Điều hướng về home
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    fun registerUser(userId: String, userInfo: InfoUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val updatedUser = userInfo.copy(userId = userId, avatarUrl = null) // Không có avatarUrl
        saveUserInfo(updatedUser, userId, onSuccess, onFailure)
    }
}