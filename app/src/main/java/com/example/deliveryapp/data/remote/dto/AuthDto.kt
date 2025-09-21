package com.example.deliveryapp.data.remote.dto

data class AuthResponseDto(
    val access_token: String,
    val refresh_token: String
)

data class LoginRequestDto(val email: String, val password: String)
data class SignupRequestDto(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String
)