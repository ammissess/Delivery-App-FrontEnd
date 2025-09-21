package com.example.deliveryapp.data.remote.interceptor

import com.example.deliveryapp.data.local.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.Request
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val dataStore: DataStoreManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = runBlocking { dataStore.accessToken.first() }
        val authRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else request
        return chain.proceed(authRequest)
    }
}