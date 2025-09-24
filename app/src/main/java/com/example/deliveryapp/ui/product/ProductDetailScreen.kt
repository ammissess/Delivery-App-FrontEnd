package com.example.deliveryapp.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deliveryapp.data.remote.dto.ProductDto  // Thêm import này
import com.example.deliveryapp.data.remote.dto.ProductImageDto  // Thêm import này (nếu chưa có)
import com.example.deliveryapp.utils.Resource

@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: Long,
    viewModel: ProductViewModel = hiltViewModel()
) {
    // Load product detail theo productId
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    val state by viewModel.product.collectAsState()

    when (state) {
        is Resource.Loading -> CircularProgressIndicator()
        is Resource.Error -> Text((state as Resource.Error).message ?: "Error")
        is Resource.Success -> {
            val product = (state as Resource.Success).data
            if (product == null) {  // Check null
                Text("Product not found")
                return
            }
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Safe extract main image URL (ưu tiên is_main = true, fallback first image)
                val mainImageUrl = product.images.firstOrNull { it.is_main }?.url
                    ?: product.images.firstOrNull()?.url
                    ?: ""  // Nếu không có, dùng empty string (AsyncImage sẽ handle)

                AsyncImage(
                    model = mainImageUrl.ifEmpty { "https://via.placeholder.com/300" },  // Fallback placeholder nếu URL rỗng
                    contentDescription = product.name,  // Tốt hơn null cho accessibility
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    error = painterResource(id = android.R.drawable.ic_menu_gallery)  // Error fallback
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = "$${product.price}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = product.description.orEmpty())  // Safe cho nullable
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    // TODO: Navigate to order với product này (ví dụ: navController.navigate("order?productId=${product.id}"))
                }) {
                    Text("Place Order")
                }
            }
        }
    }
}