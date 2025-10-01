package com.example.deliveryapp.data.repository

import com.example.deliveryapp.data.remote.api.OrderApi
import com.example.deliveryapp.data.remote.api.OrderSummaryDto
import com.example.deliveryapp.data.remote.dto.OrderDetailDto
import com.example.deliveryapp.data.remote.dto.PlaceOrderRequestDto
import com.example.deliveryapp.utils.Constants
import com.example.deliveryapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class OrderRepository(private val api: OrderApi) {
    suspend fun placeOrder(req: PlaceOrderRequestDto): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val resp = api.placeOrder(req)
            if (resp.isSuccessful) {
                Resource.Success(resp.body()?.message ?: "Order placed")
            } else {
                Resource.Error("Error: ${resp.code()}")
            }
        } catch (e: IOException) {
            Resource.Error("Network error")
        } catch (e: HttpException) {
            Resource.Error("Server error")
        }
    }

    suspend fun getOrders(): Resource<List<OrderSummaryDto>> = withContext(Dispatchers.IO) {
        try {
            val resp = api.getOrders()
            if (resp.isSuccessful) {
                Resource.Success(resp.body()?.orders ?: emptyList())
            } else {
                Resource.Error("Error: ${resp.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    suspend fun getOrderDetail(id: Long): Resource<OrderDetailDto> = withContext(Dispatchers.IO) {
        try {
            val resp = api.getOrderDetail(id)
            if (resp.isSuccessful) {
                resp.body()?.let { Resource.Success(it) } ?: Resource.Error("Empty body")
            } else {
                Resource.Error("Error: ${resp.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }

    //them xac thuc refesh token tao don hang
    suspend fun placeOrderWithRefreshToken(req: PlaceOrderRequestDto, refreshToken: String): Resource<String> = withContext(Dispatchers.IO) {
        try {
            // Tạo custom OkHttpClient có thêm refresh token header
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-Refresh-Token", refreshToken)
                        .build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val customApi = retrofit.create(OrderApi::class.java)
            val resp = customApi.placeOrder(req)

            if (resp.isSuccessful) {
                Resource.Success(resp.body()?.message ?: "Đặt hàng thành công")
            } else {
                Resource.Error("Error: ${resp.code()}")
            }
        } catch (e: IOException) {
            Resource.Error("Lỗi mạng")
        } catch (e: HttpException) {
            Resource.Error("Lỗi server")
        }
    }
}