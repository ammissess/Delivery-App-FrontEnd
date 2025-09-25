package com.example.deliveryapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deliveryapp.data.remote.dto.ProductDto
import com.example.deliveryapp.ui.navigation.Screen
import com.example.deliveryapp.utils.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Thu thập states từ ViewModel
    val productsState by homeViewModel.products.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val categories by homeViewModel.categories.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    var selectedTab by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        selectedTab = 0
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Danh mục",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                categories.forEach { category ->
                    Text(
                        text = category,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { drawerState.close() }
                                homeViewModel.fetchProductsByCategory(category)
                            }
                            .padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            homeViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất")
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedCategory.ifEmpty { "Tất cả sản phẩm" }) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    selectedTab = selectedTab,
                    onTabSelected = { newTab -> selectedTab = newTab }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        homeViewModel.searchProducts(it.text)
                    },
                    label = { Text("Tìm sản phẩm...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                when (productsState) {
                    is Resource.Loading -> {
                        if (productsState.data == null || productsState.data?.isEmpty() == true) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            ProductGrid(
                                products = productsState.data as List<ProductDto>,
                                onProductClick = { product ->
                                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                                },
                                isLoading = isLoading
                            )
                        }
                    }
                    is Resource.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(productsState.message ?: "Đã xảy ra lỗi")
                        }
                    }
                    is Resource.Success -> {
                        val products = productsState.data as? List<ProductDto> ?: emptyList()
                        if (products.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Không có sản phẩm")
                            }
                        } else {
                            ProductGrid(
                                products = products,
                                onProductClick = { product ->
                                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                                },
                                isLoading = isLoading
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun ProductGrid(
    products: List<ProductDto>,
    onProductClick: (ProductDto) -> Unit,
    isLoading: Boolean
) {
    val lazyGridState = rememberLazyGridState()

    Box {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products, key = { it.id }) { product: ProductDto ->
                ProductItemDelivery(product = product, onClick = { onProductClick(product) })
            }
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun ProductItemDelivery(product: ProductDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            val mainImage = product.images.firstOrNull { it.is_main }?.url
                ?: product.images.firstOrNull()?.url

            AsyncImage(
                model = mainImage ?: "",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = product.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}
