package com.example.deliveryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.deliveryapp.ui.auth.*
import com.example.deliveryapp.ui.home.HomeScreen
import com.example.deliveryapp.ui.product.ProductDetailScreen
import com.example.deliveryapp.ui.messages.MessagesScreen
import com.example.deliveryapp.ui.order.OrderStatusScreen
import com.example.deliveryapp.ui.profile.ProfileScreen
import com.example.deliveryapp.ui.auth.SplashScreen   // ✅ import Splash

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route // ✅ Splash mặc định
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ✅ Splash route
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        // Authentication routes
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Signup.route) { SignupScreen(navController) }

        composable(
            route = "otp_verify/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpVerifyScreen(navController, email)
        }

        composable("forgot_password") { ForgotPasswordScreen(navController) }

        composable(
            route = "reset_password/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController, email)
        }

        // Main app routes
        composable(Screen.Home.route) { HomeScreen(navController) }

        composable("messages") { MessagesScreen(navController) }

        composable("orders") {
            // bạn có thể truyền orderId thực tế khi navigate
            OrderStatusScreen(orderId = 0L)
        }

        composable("profile") { ProfileScreen(navController) }

        // Product detail route
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(navController, productId = id)
        }
    }
}