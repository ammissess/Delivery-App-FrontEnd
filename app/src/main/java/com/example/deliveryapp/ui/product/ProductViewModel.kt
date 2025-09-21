package com.example.deliveryapp.ui.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.remote.dto.ProductDto
import com.example.deliveryapp.domain.usecase.GetProductDetailUseCase
import com.example.deliveryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProduct: GetProductDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _product = MutableStateFlow<Resource<ProductDto>>(Resource.Loading())
    val product: StateFlow<Resource<ProductDto>> = _product

    init {
        val id = savedStateHandle.get<Long>("productId") ?: 0L
        if (id > 0) loadProduct(id)
    }

    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _product.value = getProduct(id)
        }
    }
}