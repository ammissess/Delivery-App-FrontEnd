package com.example.deliveryapp.domain.usecase

import com.example.deliveryapp.data.remote.dto.ProductDto
import com.example.deliveryapp.data.repository.ProductRepository
import com.example.deliveryapp.utils.Resource

class GetProductsUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 20): Resource<List<ProductDto>> = repo.getProducts(page, limit)
}