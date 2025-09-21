package com.example.deliveryapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.deliveryapp.utils.Resource

@Composable
fun SignupScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val state by viewModel.signupState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        TextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.signup(name, email, password, phone, address) }) { Text("Signup") }
        if (state is Resource.Success) navController.navigate("login")
        if (state is Resource.Error) Text((state as Resource.Error).message ?: "Error")
    }
}