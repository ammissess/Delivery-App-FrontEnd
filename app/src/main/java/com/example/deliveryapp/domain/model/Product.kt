package com.example.deliveryapp.domain.model

data class Product(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val images: List<String> = emptyList()
)