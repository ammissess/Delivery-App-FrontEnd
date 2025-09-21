package com.example.deliveryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.deliveryapp.ui.auth.LoginScreen
import com.example.deliveryapp.ui.auth.SignupScreen
import com.example.deliveryapp.ui.home.HomeScreen
import com.example.deliveryapp.ui.order.OrderStatusScreen
import com.example.deliveryapp.ui.product.ProductDetailScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController)  //{ navController.navigate(Screen.Home.route) }
        }
        composable(Screen.Signup.route) {
            SignupScreen(navController) //{ navController.navigate(Screen.Login.route) }
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(navController, productId = id)
        }
        composable(Screen.OrderStatus.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("orderId") ?: 0L
            OrderStatusScreen(id)
        }
    }
}