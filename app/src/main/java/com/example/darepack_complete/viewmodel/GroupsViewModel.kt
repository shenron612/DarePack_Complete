package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.Group
import com.example.darepack_complete.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GroupsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    private val _members = MutableStateFlow<List<UserModel>>(emptyList())
    val members: StateFlow<List<UserModel>> = _members

    init {
        loadUserGroups()
    }

    private fun loadUserGroups() {
        val userId = auth.currentUser?.uid ?: return
        db.getReference("Users/$userId/groups").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupIds = snapshot.children.mapNotNull { it.key }
                fetchGroupsDetails(groupIds)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchGroupsDetails(groupIds: List<String>) {
        viewModelScope.launch {
            val fetchedGroups = groupIds.mapNotNull { gid ->
                try {
                    db.getReference("Groups/$gid").get().await().getValue(Group::class.java)
                } catch (e: Exception) {
                    null
                }
            }
            _groups.value = fetchedGroups
        }
    }

    fun loadMembers(memberIds: List<String>) {
        viewModelScope.launch {
            val fetchedMembers = memberIds.mapNotNull { mid ->
                try {
                    db.getReference("Users/$mid").get().await().getValue(UserModel::class.java)
                } catch (e: Exception) {
                    null
                }
            }
            _members.value = fetchedMembers.sortedByDescending { it.totalCompleted }
        }
    }

    fun joinGroup(groupId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val groupRef = db.getReference("Groups/$groupId")
                val group = groupRef.get().await().getValue(Group::class.java) ?: return@launch
                
                if (!group.members.contains(userId)) {
                    val newMembers = group.members + userId
                    groupRef.child("members").setValue(newMembers).await()
                    db.getReference("Users/$userId/groups").child(groupId).setValue(true).await()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
