package com.example.deliveryapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deliveryapp.data.remote.dto.ProductDto
import com.example.deliveryapp.utils.Resource

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.products.collectAsState()

    when (state) {
        is Resource.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
        is Resource.Error -> Text((state as Resource.Error).message ?: "Error")
        is Resource.Success -> {
            val products = (state as Resource.Success<List<ProductDto>>).data!!
            LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                items(products) { product ->
                    ProductItem(product) { navController.navigate("product/${product.id}") }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductDto, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }) {
        Row(modifier = Modifier.padding(12.dp)) {
            val img = product.images.firstOrNull()?.url
            AsyncImage(model = img ?: "", contentDescription = null, modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "$${product.price}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}