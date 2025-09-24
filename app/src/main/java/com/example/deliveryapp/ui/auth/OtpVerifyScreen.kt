package com.example.deliveryapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.deliveryapp.utils.Resource

@Composable
fun OtpVerifyScreen(navController: NavController, email: String, viewModel: AuthViewModel = hiltViewModel()) {
    var otp by remember { mutableStateOf("") }
    val state by viewModel.verifyOtpState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verify Email", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text("Enter OTP sent to $email")
        TextField(value = otp, onValueChange = { otp = it }, label = { Text("OTP") })

        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.verifyOtp(email, otp) }) { Text("Verify") }

        when (state) {
            null -> {}
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Error -> Text("Error: ${(state as Resource.Error).message}", color = Color.Red)
            is Resource.Success -> {
                LaunchedEffect(state) {
                    navController.navigate("login") {
                        popUpTo("otp_verify/$email") { inclusive = true }
                    }
                }
            }
        }
    }
}