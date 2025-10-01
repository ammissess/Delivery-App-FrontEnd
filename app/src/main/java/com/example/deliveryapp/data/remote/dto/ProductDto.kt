package com.example.deliveryapp.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
//import java.sql.Date
import java.util.Date

@Parcelize
data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("qty_initial") val qty_initial: Long?,
    @SerializedName("qty_sold") val qty_sold: Long?,
<<<<<<< HEAD
    @SerializedName("created_at") val created_at: Date?,
    val images: List<ProductImageDto> = emptyList()
) : Parcelable
=======
    @SerializedName("created_at") val created_at: Date?,  // Hoặc String? nếu parse sau
    val images: List<ProductImageDto> = emptyList(),
    val avgRate: Double = 0.0,
    val reviewCount: Int = 0
)
>>>>>>> 14a83287a4d5d90fb078d1849622c3bac5e79eb8

@Parcelize
data class ProductImageDto(
    @SerializedName("ID") val id: Long,
    @SerializedName("URL") val url: String,
    @SerializedName("IsMain") val is_main: Boolean
) : Parcelable