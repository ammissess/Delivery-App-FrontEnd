package com.example.deliveryapp.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.deliveryapp.data.remote.dto.SignupRequestDto
import com.example.deliveryapp.utils.Resource
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val state by viewModel.signupState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🚚 Create Account", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
                        viewModel.signup(SignupRequestDto(name, email, password, phone, address))
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("❌ Please fill in all fields") }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Sign Up") }

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? → Login")
            }

            AnimatedVisibility(visible = state is Resource.Loading) {
                CircularProgressIndicator()
            }

            when (val s = state) {
                is Resource.Error -> {
                    LaunchedEffect(s) {
                        scope.launch { snackbarHostState.showSnackbar(s.message ?: "Signup failed") }
                    }
                }
                is Resource.Success -> {
                    LaunchedEffect(s) {
                        scope.launch { snackbarHostState.showSnackbar("✅ Account created! Verify your email.") }
                        navController.navigate("otp_verify/$email")
                    }
                }
                else -> {}
            }
        }
    }
}