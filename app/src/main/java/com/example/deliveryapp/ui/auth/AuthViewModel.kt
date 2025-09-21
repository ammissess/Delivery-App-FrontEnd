package com.example.deliveryapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deliveryapp.data.remote.dto.LoginRequestDto
import com.example.deliveryapp.data.remote.dto.SignupRequestDto
import com.example.deliveryapp.data.repository.AuthRepository
import com.example.deliveryapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val loginState: StateFlow<Resource<Unit>> = _loginState

    private val _signupState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val signupState: StateFlow<Resource<Unit>> = _signupState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            when (val result = repo.login(LoginRequestDto(email, password))) {
                is Resource.Success -> {
                    // login thành công => chỉ quan tâm "đã success", convert sang Unit
                    _loginState.value = Resource.Success(Unit)
                }
                is Resource.Error -> {
                    _loginState.value = Resource.Error(result.message ?: "Unknown error")
                }
                is Resource.Loading -> {
                    _loginState.value = Resource.Loading()
                }
            }
        }
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String
    ) {
        viewModelScope.launch {
            _signupState.value = Resource.Loading()
            when (val result = repo.signup(SignupRequestDto(name, email, password, phone, address))) {
                is Resource.Success -> {
                    _signupState.value = Resource.Success(Unit)
                }
                is Resource.Error -> {
                    _signupState.value = Resource.Error(result.message ?: "Unknown error")
                }
                is Resource.Loading -> {
                    _signupState.value = Resource.Loading()
                }
            }
        }
    }
}
