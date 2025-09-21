package com.example.deliveryapp.data.repository

import com.example.deliveryapp.data.local.DataStoreManager
import com.example.deliveryapp.data.remote.api.AuthApi
import com.example.deliveryapp.data.remote.dto.AuthResponseDto
import com.example.deliveryapp.data.remote.dto.LoginRequestDto
import com.example.deliveryapp.data.remote.dto.SignupRequestDto
import com.example.deliveryapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val api: AuthApi,
    private val dataStore: DataStoreManager
) {
    suspend fun login(req: LoginRequestDto): Resource<AuthResponseDto> = withContext(Dispatchers.IO) {
        try {
            val resp = api.login(req)
            if (resp.isSuccessful) {
                resp.body()?.let { dto ->
                    dataStore.saveTokens(dto.access_token, dto.refresh_token)
                    Resource.Success(dto)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Login failed: ${resp.code()}")
            }
        } catch (e: IOException) {
            Resource.Error("Network error")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.code()}")
        }
    }

    suspend fun signup(req: SignupRequestDto): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = api.signup(req)
            if (resp.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Signup failed: ${resp.code()}")
            }
        } catch (e: IOException) {
            Resource.Error("Network error")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.code()}")
        }
    }

    suspend fun refreshToken(refresh: String): Resource<AuthResponseDto> = withContext(Dispatchers.IO) {
        try {
            val resp = api.refreshToken(refresh)
            if (resp.isSuccessful) {
                resp.body()?.let { dto ->
                    dataStore.saveTokens(dto.access_token, dto.refresh_token)
                    Resource.Success(dto)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Refresh failed: ${resp.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    suspend fun logout() {
        dataStore.clearTokens()
    }
}