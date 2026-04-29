package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import com.example.darepack_complete.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    fun signOut() {
        // TODO: sign out logic
    }
}
