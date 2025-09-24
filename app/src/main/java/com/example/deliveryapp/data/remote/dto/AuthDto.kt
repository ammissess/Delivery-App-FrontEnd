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

// Verify OTP request (đăng ký/email verify)
data class VerifyOtpRequestDto(
    val email: String,
    val otp: String
)

// Forgot password request
data class ForgotPasswordRequestDto(
    val email: String
)

// Reset password request
data class ResetPasswordDto(
    val token: String,
    val new_password: String
)

// Reset password response (server trả về token)
data class ResetTokenDto(
    val reset_token: String,
    val expires_in: Int
)
