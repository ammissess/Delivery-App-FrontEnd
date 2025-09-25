package com.example.deliveryapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date  // Nếu backend trả Date, hoặc dùng String? nếu là ISO string

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("qty_initial") val qty_initial: Long?,
    @SerializedName("qty_sold") val qty_sold: Long?,
    @SerializedName("created_at") val created_at: Date?,  // Hoặc String? nếu parse sau
    val images: List<ProductImageDto> = emptyList()
)

data class ProductImageDto(
    @SerializedName("ID") val id: Long,  // Match JSON "ID"
    @SerializedName("URL") val url: String,  // Match "URL"
    @SerializedName("IsMain") val is_main: Boolean  // Match "IsMain"
)
