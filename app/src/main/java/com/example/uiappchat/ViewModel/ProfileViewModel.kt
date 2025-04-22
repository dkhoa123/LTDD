package com.example.uiappchat.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uiappchat.Model.InfoUser
import com.example.uiappchat.Model.ProfileUiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ProfileViewModel : ViewModel() {

    // Trạng thái UI
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    // Các trạng thái hiện có
    private val _userInfo = MutableStateFlow<InfoUser?>(null)
    val userInfo: StateFlow<InfoUser?> = _userInfo

    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl

    private val auth = FirebaseAuth.getInstance()

    // Lấy thông tin người dùng
    fun fetchUserInfo(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .get()
                    .await()

                val user = snapshot.getValue(InfoUser::class.java)
                _userInfo.value = user

                val photo = snapshot.child("avatarUrl").getValue(String::class.java)
                _photoUrl.value = photo ?: auth.currentUser?.photoUrl?.toString()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Lỗi khi tải thông tin: ${e.message}")
            }
        }
    }

    // Hàm đăng xuất
    fun signOut(context: Context, authViewModel: AuthViewModel) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val currentUserId = auth.currentUser?.uid
                if (currentUserId != null) {
                    try {
                        FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(currentUserId)
                            .child("online")
                            .setValue(false)
                            .await()
                    } catch (e: Exception) {
                        Log.w("SignOut", "Không thể cập nhật trạng thái offline: ${e.message}")
                    }
                }

                auth.signOut()

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("656943469501-d3m240a31t375aih1lk04dovra457gvm.apps.googleusercontent.com")
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                val googleAccount = GoogleSignIn.getLastSignedInAccount(context)

                if (googleAccount != null) {
                    try {
                        googleSignInClient.signOut().await()
                        googleSignInClient.revokeAccess().await()
                        Log.d("SignOut", "Đăng xuất Google thành công")
                    } catch (e: Exception) {
                        Log.w("SignOut", "Lỗi đăng xuất Google: ${e.message}")
                    }
                }

                clear()
                authViewModel.refreshAuthState() // Cập nhật trạng thái đăng nhập

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSignedOut = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Lỗi không xác định khi đăng xuất: ${e.message}"
                )
                Log.e("SignOut", "Lỗi không xác định: ${e.message}")
            }
        }
    }

    // Xóa dữ liệu trong ViewModel
    fun clear() {
        _userInfo.value = null
        _photoUrl.value = null
    }
}

