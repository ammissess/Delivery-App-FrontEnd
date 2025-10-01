package com.example.deliveryapp.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deliveryapp.ui.home.CartItem
import com.example.deliveryapp.ui.home.formatPrice
import com.example.deliveryapp.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cart: List<CartItem>,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    var paymentMethod by remember { mutableStateOf("unpaid") } // "unpaid" hoặc "paid"
    var showEditAddress by remember { mutableStateOf(false) }

    val profileState by viewModel.profileState.collectAsState()
    val confirmState by viewModel.confirmOrderState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    // Xử lý kết quả đặt hàng
    LaunchedEffect(confirmState) {
        if (confirmState is Resource.Success) {
            // Clear cart và navigate về home
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    // Lắng nghe địa chỉ mới từ LocationPicker
    LaunchedEffect(navController.currentBackStackEntry) {
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            val lat = handle.get<Double>("selectedLat")
            val lng = handle.get<Double>("selectedLng")
            val address = handle.get<String>("selectedAddress")

            if (lat != null && lng != null && address != null) {
                viewModel.updateDeliveryAddress(lat, lng, address)
                handle.remove<Double>("selectedLat")
                handle.remove<Double>("selectedLng")
                handle.remove<String>("selectedAddress")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xác nhận đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val profile = profileState) {
            is Resource.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    Text("Lỗi: ${profile.message}")
                }
            }
            is Resource.Success -> {
                val user = profile.data
                if (user == null) return@Scaffold

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Thông tin nhận hàng
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Thông tin nhận hàng", style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = {
                                    navController.navigate("location_picker")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                                }
                            }
                            Spacer(Modifier.height(8.dp))

                            val deliveryInfo by viewModel.deliveryInfo.collectAsState()

                            Text("Người nhận: ${user.name}")
                            Text("SĐT: ${user.phone ?: "Chưa cập nhật"}")
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(deliveryInfo.address ?: user.address ?: "Chưa có địa chỉ")
                            }
                        }
                    }

                    // Danh sách sản phẩm
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Sản phẩm đã chọn", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))

                            cart.forEach { item ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = item.product.images.firstOrNull()?.url,
                                        contentDescription = item.product.name,
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(item.product.name, style = MaterialTheme.typography.bodyLarge)
                                        Text("x${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text(
                                        formatPrice(item.product.price * item.quantity),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (cart.last() != item) Divider()
                            }
                        }
                    }

                    // Phương thức thanh toán
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Phương thức thanh toán", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))

                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == "unpaid",
                                    onClick = { paymentMethod = "unpaid" }
                                )
                                Text("Thanh toán khi nhận hàng")
                            }

                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == "paid",
                                    onClick = { paymentMethod = "paid" }
                                )
                                Text("Chuyển khoản")
                            }
                        }
                    }

                    // Tổng tiền
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tổng cộng:", style = MaterialTheme.typography.titleLarge)
                            Text(
                                formatPrice(cart.sumOf { it.product.price * it.quantity }),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Nút xác nhận
                    Button(
                        onClick = {
                            val deliveryInfo = viewModel.deliveryInfo.value
                            viewModel.confirmOrder(
                                cart = cart,
                                latitude = deliveryInfo.latitude ?: 0.0,
                                longitude = deliveryInfo.longitude ?: 0.0,
                                paymentMethod = paymentMethod
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = confirmState !is Resource.Loading
                    ) {
                        when (confirmState) {
                            is Resource.Loading -> {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("Đang xử lý...")
                                }
                            }
                            else -> Text("Xác nhận đặt hàng")
                        }
                    }

                    // Thông báo lỗi
                    if (confirmState is Resource.Error) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = (confirmState as Resource.Error).message ?: "Lỗi đặt hàng",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}