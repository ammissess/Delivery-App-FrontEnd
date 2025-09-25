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

    // State cho chi tiết sản phẩm
    private val _product = MutableStateFlow<Resource<ProductDto>>(Resource.Loading())
    val product: StateFlow<Resource<ProductDto>> = _product

    // Loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Thông báo lỗi
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        val id = savedStateHandle.get<Long>("productId") ?: 0L
        if (id > 0) {
            loadProduct(id)
        } else {
            _product.value = Resource.Error("Không tìm thấy ID sản phẩm")
            _errorMessage.value = "ID sản phẩm không hợp lệ"
        }
    }

    /** Load chi tiết sản phẩm theo ID */
    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _product.value = getProduct(id)
                if (_product.value !is Resource.Success) {
                    _errorMessage.value = (_product.value as? Resource.Error)?.message ?: "Lỗi tải sản phẩm"
                }
            } catch (e: Exception) {
                _product.value = Resource.Error(e.message ?: "Lỗi tải sản phẩm")
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Reload sản phẩm (ví dụ: sau khi update) */
    fun reloadProduct(id: Long) {
        loadProduct(id)
    }
}