package com.example.deliveryapp.data.remote.api

import com.example.deliveryapp.data.remote.dto.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ProductsListResponse>


    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<ProductWrapper>
}

data class ProductsListResponse(
    val products: List<ProductDto>,
    val pagination: Pagination?
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val total_pages: Int
)

data class ProductWrapper(
    val product: ProductDto
)