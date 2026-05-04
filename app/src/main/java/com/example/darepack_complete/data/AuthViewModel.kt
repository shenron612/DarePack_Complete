package com.example.darepack_complete.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// Create this sealed class to handle navigation/toasts
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    object NavigateToHome : UiEvent()
    object NavigateToLogin : UiEvent()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // UI Event Channel to communicate with the Screen
    private val _eventChannel = Channel<UiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun signup(username: String, email: String, phone: String, password: String, confirmPassword: String) {
        if (username.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            sendEvent(UiEvent.ShowToast("Please fill all fields"))
            return
        }
        if (password != confirmPassword) {
            sendEvent(UiEvent.ShowToast("Passwords do not match"))
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = UserModel(username, email, phone, auth.currentUser?.uid ?: "")
                saveUserToDatabase(user)
            } else {
                sendEvent(UiEvent.ShowToast(task.exception?.message ?: "Signup failed"))
            }
        }
    }

    private fun saveUserToDatabase(user: UserModel) {
        FirebaseDatabase.getInstance().getReference("Users/${user.userId}")
            .setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEvent(UiEvent.ShowToast("Registration Successful"))
                    sendEvent(UiEvent.NavigateToLogin)
                } else {
                    sendEvent(UiEvent.ShowToast("Failed to save data"))
                }
            }
    }

    private fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _eventChannel.send(event) }
    }
}