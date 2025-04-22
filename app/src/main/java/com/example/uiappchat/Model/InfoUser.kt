package com.example.uiappchat.Model

data class InfoUser(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",  // Sẽ lưu email để khớp với tài khoản Firebase Authentication
    val age: String = "",
    val address: String = "",
    val sex: String = "",
    val userId: String? = null, // Sẽ cập nhật sau khi đăng ký thành công
    val avatarUrl: String? = null
)
