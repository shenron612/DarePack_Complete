package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class CreateGroupState {
    object Idle : CreateGroupState()
    object Loading : CreateGroupState()
    data class Success(val groupId: String) : CreateGroupState()
    data class Error(val message: String) : CreateGroupState()
}

class CreateGroupViewModel : ViewModel() {
    private val _state = MutableStateFlow<CreateGroupState>(CreateGroupState.Idle)
    val state: StateFlow<CreateGroupState> = _state

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    fun createGroup(name: String) {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _state.value = CreateGroupState.Loading
            try {
                val groupRef = db.getReference("Groups").push()
                val groupId = groupRef.key ?: throw Exception("Failed to get group ID")
                
                // Simple random invite code
                val inviteCode = (100000..999999).random().toString()
                
                val group = Group(
                    groupId = groupId,
                    name = name,
                    inviteCode = inviteCode,
                    members = listOf(userId)
                )
                
                groupRef.setValue(group).await()
                
                // Also add group reference to user's profile
                db.getReference("Users/$userId/groups").child(groupId).setValue(true).await()
                
                _state.value = CreateGroupState.Success(groupId)
            } catch (e: Exception) {
                _state.value = CreateGroupState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _state.value = CreateGroupState.Idle
    }
}
