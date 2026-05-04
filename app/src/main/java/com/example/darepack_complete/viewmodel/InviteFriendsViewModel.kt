package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.Group
import com.example.darepack_complete.models.UserModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class InviteFriendsViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()

    private val _group = MutableStateFlow<Group?>(null)
    val group: StateFlow<Group?> = _group

    private val _searchResults = MutableStateFlow<List<UserModel>>(emptyList())
    val searchResults: StateFlow<List<UserModel>> = _searchResults

    fun loadGroup(groupId: String) {
        viewModelScope.launch {
            try {
                val g = db.getReference("Groups/$groupId").get().await().getValue(Group::class.java)
                _group.value = g
            } catch (e: Exception) {}
        }
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                // This is a very basic search (matches email prefix)
                val snapshot = db.getReference("Users")
                    .orderByChild("email")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .get().await()
                
                val users = snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
                _searchResults.value = users
            } catch (e: Exception) {}
        }
    }
    
    fun inviteUser(userId: String) {
        val groupId = _group.value?.groupId ?: return
        viewModelScope.launch {
            try {
                // In a real app, we'd send a notification. 
                // Here we just add them to the group directly for simplicity
                val groupRef = db.getReference("Groups/$groupId")
                val group = _group.value ?: return@launch
                if (!group.members.contains(userId)) {
                    val newMembers = group.members + userId
                    groupRef.child("members").setValue(newMembers).await()
                    db.getReference("Users/$userId/groups").child(groupId).setValue(true).await()
                    // Refresh local state
                    _group.value = group.copy(members = newMembers)
                }
            } catch (e: Exception) {}
        }
    }
}
