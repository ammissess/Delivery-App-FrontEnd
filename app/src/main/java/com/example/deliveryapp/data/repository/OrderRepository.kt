package com.example.deliveryapp.data.repository

import com.example.deliveryapp.data.remote.api.OrderApi
import com.example.deliveryapp.data.remote.api.OrderSummaryDto
import com.example.deliveryapp.data.remote.dto.OrderDetailDto
import com.example.deliveryapp.data.remote.dto.PlaceOrderRequestDto
import com.example.deliveryapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
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
}