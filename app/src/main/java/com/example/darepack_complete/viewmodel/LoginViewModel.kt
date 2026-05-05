package com.example.darepack_complete.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
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
    private val storage = FirebaseStorage.getInstance()

    fun signUp(name: String, gender: String, email: String, pass: String, confirmPass: String, imageUri: Uri?) {
        if (name.isBlank() || gender.isBlank() || email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("Please fill all fields")
            return
        }
        if (pass != confirmPass) {
            _loginState.value = LoginState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val userId = result.user?.uid ?: throw Exception("Failed to get User ID")
                
                var photoUrl = ""
                if (imageUri != null && imageUri != Uri.EMPTY) {
                    try {
                        val ref = storage.reference.child("profile_pics/$userId")
                        ref.putFile(imageUri).await()
                        photoUrl = ref.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        // Log storage error but continue with registration if possible
                        // or fail if you require a profile pic
                        e.printStackTrace()
                        // If you want to fail on storage error, uncomment next line:
                        // throw Exception("Failed to upload image: ${e.localizedMessage}")
                    }
                }

                val user = UserModel(
                    userId = userId,
                    name = name,
                    username = name.lowercase().trim().replace(" ", "_"),
                    gender = gender,
                    email = email,
                    photoUrl = photoUrl
                )
                
                db.getReference("Users").child(userId).setValue(user).await()
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                e.printStackTrace()
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
