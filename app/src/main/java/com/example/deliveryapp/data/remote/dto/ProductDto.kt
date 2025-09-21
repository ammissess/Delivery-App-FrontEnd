package com.example.deliveryapp.data.remote.dto

import java.util.Date

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val qty_initial: Long?,
    val qty_sold: Long?,
    val created_at: Date?,
    val images: List<ProductImageDto> = emptyList()
)

data class ProductImageDto(
    val id: Long,
    val url: String,
    val is_main: Boolean
)