package com.example.deliveryapp.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.deliveryapp.utils.Resource
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    val state by viewModel.forgotPassState.collectAsState()
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
            Text("🔐 Forgot Password?", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.forgotPassword(email) }, modifier = Modifier.fillMaxWidth()) {
                Text("Send OTP")
            }

            AnimatedVisibility(visible = state is Resource.Loading) {
                CircularProgressIndicator()
            }

            when (val s = state) {
                is Resource.Error -> LaunchedEffect(s) { scope.launch { snackbarHostState.showSnackbar("❌ ${s.message}") } }
                is Resource.Success -> LaunchedEffect(s) {
                    scope.launch { snackbarHostState.showSnackbar("✅ OTP sent to $email") }
                    navController.navigate("reset_password/$email")
                }
                else -> {}
            }
        }
    }
}