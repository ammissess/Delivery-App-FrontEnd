package com.example.deliveryapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.remote.dto.ProductDto
import com.example.deliveryapp.domain.usecase.GetProductsUseCase
import com.example.deliveryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val getProducts: GetProductsUseCase) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<ProductDto>>>(Resource.Loading())
    val products: StateFlow<Resource<List<ProductDto>>> = _products

    init {
        fetchProducts()
    }

    fun fetchProducts(page: Int = 1) {
        viewModelScope.launch {
            _products.value = getProducts(page)
        }
    }
}