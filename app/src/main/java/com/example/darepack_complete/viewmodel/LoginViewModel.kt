package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun signUp(name: String, gender: String, email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            // TODO: Implement actual Firebase Auth registration
            delay(1500) // Simulate network delay
            _loginState.value = LoginState.Success
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            // TODO: Implement actual Firebase Auth login
            delay(1500)
            _loginState.value = LoginState.Success
        }
    }
}
