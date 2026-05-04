package com.example.darepack_complete.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    fun signUp(name: String, gender: String, email: String, pass: String, imageUri: Uri?) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val userId = result.user?.uid ?: throw Exception("Failed to get User ID")
                
                val user = UserModel(
                    userId = userId,
                    name = name,
                    gender = gender,
                    email = email
                )
                
                db.getReference("Users").child(userId).setValue(user).await()
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                e.printStackTrace() // Log full stack trace for debugging
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Sign up failed")
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Login failed")
            }
        }
    }
}
