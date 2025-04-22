package com.example.uiappchat.Model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("noti/send")
    suspend fun sendNoti(@Body req: NotiRequest): Response<Unit>
}