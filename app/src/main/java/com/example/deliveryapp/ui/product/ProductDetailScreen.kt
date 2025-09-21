package com.example.deliveryapp.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
            val product = (state as Resource.Success).data!!
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val img = product.images.firstOrNull()?.url
                AsyncImage(
                    model = img.orEmpty(), // tránh lỗi String? -> String
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(product.name, style = MaterialTheme.typography.headlineSmall)
                Text("$${product.price}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(product.description.orEmpty())
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { /* TODO: Navigate to order */ }) {
                    Text("Place Order")
                }
            }
        }
    }
}
