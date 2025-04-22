package com.example.uiappchat.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        // Lắng nghe thay đổi trạng thái đăng nhập từ Firebase
        auth.addAuthStateListener { firebaseAuth ->
            viewModelScope.launch {
                val user = firebaseAuth.currentUser
                _isLoggedIn.value = user != null
                _userId.value = user?.uid
                Log.d("AuthViewModel", "userId: ${_userId.value}, isLoggedIn: ${_isLoggedIn.value}")
            }
        }

        // Cập nhật trạng thái ban đầu
        viewModelScope.launch {
            _userId.value = auth.currentUser?.uid
            _isLoggedIn.value = auth.currentUser != null
        }
    }

    fun refreshAuthState() {
        viewModelScope.launch {
            _userId.value = auth.currentUser?.uid
            _isLoggedIn.value = auth.currentUser != null
            Log.d("AuthViewModel", "Refreshed - userId: ${_userId.value}, isLoggedIn: ${_isLoggedIn.value}")
        }
    }
}