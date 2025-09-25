package com.example.deliveryapp.ui.product

import androidx.compose.foundation.ExperimentalFoundationApi  // THÊM: OptIn cho HorizontalPager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape  // THÊM: Cho custom dot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deliveryapp.data.remote.dto.ProductDto
import com.example.deliveryapp.data.remote.dto.ProductImageDto
import com.example.deliveryapp.utils.Resource

@OptIn(ExperimentalFoundationApi::class)  // THÊM: OptIn cho toàn file (xử lý warning experimental)
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
            if (product == null) {
                Text("Product not found")
                return
            }
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Gallery: Hiển thị TẤT CẢ images từ product.images
                ProductImageGallery(images = product.images)

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = product.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = "$${product.price}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = product.description.orEmpty())
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    // TODO: Navigate to order với product này
                }) {
                    Text("Place Order")
                }
            }
        }
    }
}

/** Composable riêng cho gallery hình ảnh (hiển thị tất cả images) */
@OptIn(ExperimentalFoundationApi::class)  // THÊM: OptIn cục bộ nếu cần
@Composable
fun ProductImageGallery(images: List<ProductImageDto>) {
    if (images.isEmpty()) {
        // Fallback nếu không có hình
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://via.placeholder.com/300?text=No+Image",
                contentDescription = "No image available",
                modifier = Modifier.size(200.dp)
            )
        }
    } else {
        // Sử dụng HorizontalPager cho swipe mượt
        val pagerState = rememberPagerState(pageCount = { images.size })
        Column {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) { page ->
                val image = images[page]
                AsyncImage(
                    model = image.url.ifEmpty { "https://via.placeholder.com/300" },
                    contentDescription = "${page + 1} of ${images.size}",
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = android.R.drawable.ic_menu_gallery)
                )
            }
            // Page indicator (custom dots bằng Box + CircleShape, không cần Icon Circle)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                    )
                    if (index < images.size - 1) Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}