package com.example.deliveryapp.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.local.DataStoreManager
import com.example.deliveryapp.data.remote.dto.OrderProductDto
import com.example.deliveryapp.data.remote.dto.PlaceOrderRequestDto
import com.example.deliveryapp.data.remote.dto.ProfileDto
import com.example.deliveryapp.data.repository.AuthRepository
import com.example.deliveryapp.data.repository.OrderRepository
import com.example.deliveryapp.ui.home.CartItem
import com.example.deliveryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeliveryInfo(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<ProfileDto>>(Resource.Loading())
    val profileState: StateFlow<Resource<ProfileDto>> = _profileState

    private val _confirmOrderState = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val confirmOrderState: StateFlow<Resource<String>> = _confirmOrderState

    private val _deliveryInfo = MutableStateFlow(DeliveryInfo())
    val deliveryInfo: StateFlow<DeliveryInfo> = _deliveryInfo

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = authRepository.getProfile()
            // Set địa chỉ mặc định từ profile
            if (_profileState.value is Resource.Success) {
                val profile = (_profileState.value as Resource.Success<ProfileDto>).data
                if (_deliveryInfo.value.address == null && profile != null) {
                    _deliveryInfo.value = DeliveryInfo(
                        latitude = 0.0, // Default hoặc lấy từ profile nếu có
                        longitude = 0.0,
                        address = profile.address
                    )
                }
            }
        }
    }

    fun updateDeliveryAddress(lat: Double, lng: Double, address: String) {
        _deliveryInfo.value = DeliveryInfo(lat, lng, address)
    }

    fun confirmOrder(
        cart: List<CartItem>,
        latitude: Double,
        longitude: Double,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            _confirmOrderState.value = Resource.Loading()

            try {
                // Lấy refresh token
                val refreshToken = dataStore.refreshToken.first()
                if (refreshToken.isNullOrEmpty()) {
                    _confirmOrderState.value = Resource.Error("Phiên đăng nhập hết hạn")
                    return@launch
                }

                // Tạo request
                val products = cart.map { OrderProductDto(it.product.id, it.quantity.toLong()) }
                val request = PlaceOrderRequestDto(
                    latitude = latitude,
                    longitude = longitude,
                    products = products
                )

                // Gọi API với refresh token trong header
                _confirmOrderState.value = orderRepository.placeOrderWithRefreshToken(request, refreshToken)
            } catch (e: Exception) {
                _confirmOrderState.value = Resource.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }
}