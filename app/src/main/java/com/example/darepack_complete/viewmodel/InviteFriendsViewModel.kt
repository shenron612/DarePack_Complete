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
        if (query.isBlank() || query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        val lowerQuery = query.lowercase().trim()
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        
        viewModelScope.launch {
            try {
                // Search by email
                val snapshot = db.getReference("Users")
                    .orderByChild("email")
                    .startAt(lowerQuery)
                    .endAt(lowerQuery + "\uf8ff")
                    .get().await()
                
                val emailUsers = snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
                
                // Search by name
                val nameSnapshot = db.getReference("Users")
                    .orderByChild("name")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .get().await()
                val nameUsers = nameSnapshot.children.mapNotNull { it.getValue(UserModel::class.java) }

                // Combine and filter out current user
                val combined = (emailUsers + nameUsers)
                    .distinctBy { it.userId }
                    .filter { it.userId != currentUserId }
                
                _searchResults.value = combined
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun inviteUser(userId: String) {
        val groupId = _group.value?.groupId ?: return
        viewModelScope.launch {
            try {
                val groupRef = db.getReference("Groups/$groupId")
                // Get latest group data to ensure we have the current members list
                val snapshot = groupRef.get().await()
                val currentGroup = snapshot.getValue(Group::class.java) ?: return@launch
                
                if (!currentGroup.members.contains(userId)) {
                    val newMembers = currentGroup.members + userId
                    groupRef.child("members").setValue(newMembers).await()
                    db.getReference("Users/$userId/groups").child(groupId).setValue(true).await()
                    
                    // Update local state
                    _group.value = currentGroup.copy(members = newMembers)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
