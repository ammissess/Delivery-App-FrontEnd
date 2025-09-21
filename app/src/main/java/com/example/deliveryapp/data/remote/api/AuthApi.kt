package com.example.deliveryapp.data.remote.api

import com.example.deliveryapp.data.remote.dto.AuthResponseDto
import com.example.deliveryapp.data.remote.dto.LoginRequestDto
import com.example.deliveryapp.data.remote.dto.SignupRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("signup")
    suspend fun signup(@Body req: SignupRequestDto): Response<Unit>

    @POST("login")
    suspend fun login(@Body req: LoginRequestDto): Response<AuthResponseDto>

    @POST("refresh-access-token")
    suspend fun refreshToken(@Body refreshToken: String): Response<AuthResponseDto>
}