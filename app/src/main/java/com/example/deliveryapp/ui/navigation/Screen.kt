package com.example.deliveryapp.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(id: Long) = "product/$id"
    }
    object OrderStatus : Screen("order/{orderId}") {
        fun createRoute(id: Long) = "order/$id"
    }
}