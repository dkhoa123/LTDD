package com.example.uiappchat.ViewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uiappchat.Model.InfoUser
import com.example.uiappchat.Model.SignInUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel:ViewModel() {
    // Trạng thái UI
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    private val auth = FirebaseAuth.getInstance()

    // Đăng nhập bằng email và mật khẩu
    fun signInWithEmail(email: String, password: String, context: Context) {
        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email và mật khẩu không được để trống!"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid
                if (userId != null) {
                    // Lưu trạng thái online
                    FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("online")
                        .setValue(true)

                    // Lưu avatar nếu có
                    val photoUrl = authResult.user?.photoUrl
                    if (photoUrl != null) {
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(userId)
                            .child("avatarUrl")
                            .setValue(photoUrl.toString())
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userId = userId,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Không thể lấy thông tin người dùng!"
                    )
                }
            } catch (e: Exception) {
                val errorMsg = "Tài khoản hoặc mật khẩu sai. Hãy thử lại!"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }

        }
//        Crane, a travel app by Google, is a great example of Jetpack Compose MVVM implementation. It uses ViewModels to manage UI state and business logic, separating them from composables. You can explore its source code for practical insights: https://github.com/android/compose-samples/tree/main/Crane
    }

    // Đăng nhập bằng Google
    fun signInWithGoogle(idToken: String?, context: Context) {
        if (idToken == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Không lấy được token Google!"
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val userId = authResult.user?.uid
                val firebaseUser = authResult.user
                if (userId != null) {
                    val displayName = firebaseUser?.displayName
                    val email = firebaseUser?.email
                    val avatarUrl = firebaseUser?.photoUrl?.toString()
                    val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                    userRef.child("online").setValue(true)
                    if (avatarUrl != null) {
                        userRef.child("avatarUrl").setValue(avatarUrl)
                    }
                    if (displayName != null) {
                        userRef.child("displayName").setValue(displayName)
                    }
                    if (email != null) {
                        userRef.child("email").setValue(email)
                    }
                    // Optional: Lưu tất cả thông tin người dùng dưới dạng object
                    val infoUser = InfoUser(
                        firstName = displayName ?: "",
                        lastName = "", // Không có sẵn, nếu cần bạn có thể tách tên từ displayName
                        email = email ?: "",
                        userId = userId,
                        avatarUrl = avatarUrl
                    )
                    userRef.setValue(infoUser) // Lưu đè toàn bộ infoUser nếu bạn muốn

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userId = userId,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Đăng nhập Google thất bại!"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Đăng nhập Google thất bại!"
                )
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        Log.d("GoogleLogin", "Đã nhận được idToken: $idToken")
    }

    // Reset trạng thái lỗi
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}