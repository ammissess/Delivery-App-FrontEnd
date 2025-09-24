package com.example.deliveryapp.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.deliveryapp.utils.Resource
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(navController: NavController, email: String, viewModel: AuthViewModel = hiltViewModel()) {
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    val verifyState by viewModel.verifyResetOtpState.collectAsState()
    val resetState by viewModel.resetPasswordState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Reset Password", style = MaterialTheme.typography.headlineSmall)
            Text("Enter OTP and new password for $email")
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(value = otp, onValueChange = { otp = it }, label = { Text("OTP") })
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.verifyOtpForReset(email, otp) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Verify OTP") }

            AnimatedVisibility(visible = verifyState is Resource.Loading || resetState is Resource.Loading) {
                CircularProgressIndicator()
            }

            when (val s = verifyState) {
                is Resource.Error -> LaunchedEffect(s) { scope.launch { snackbarHostState.showSnackbar("❌ ${s.message}") } }
                is Resource.Success -> {
                    val resetToken = s.data?.reset_token ?: ""
                    LaunchedEffect(resetToken, newPassword) {
                        if (newPassword.isNotBlank()) {
                            viewModel.resetPassword(resetToken, newPassword)
                        }
                    }
                }
                else -> {}
            }

            when (val s = resetState) {
                is Resource.Error -> LaunchedEffect(s) { scope.launch { snackbarHostState.showSnackbar("❌ ${s.message}") } }
                is Resource.Success -> LaunchedEffect(s) {
                    scope.launch { snackbarHostState.showSnackbar("✅ Password reset successfully!") }
                    navController.navigate("login")
                }
                else -> {}
            }
        }
    }
}