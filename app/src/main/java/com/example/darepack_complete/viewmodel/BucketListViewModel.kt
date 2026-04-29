package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.BucketItem
import com.example.darepack_complete.models.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BucketListViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    private val _items = MutableStateFlow<List<BucketItem>>(emptyList())
    val items: StateFlow<List<BucketItem>> = _items

    private val _selectedGroupId = MutableStateFlow("")
    val selectedGroupId: StateFlow<String> = _selectedGroupId

    private var itemsListener: ValueEventListener? = null

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
            if (_selectedGroupId.value.isBlank() && fetchedGroups.isNotEmpty()) {
                selectGroup(fetchedGroups.first().groupId)
            }
        }
    }

    fun selectGroup(groupId: String) {
        _selectedGroupId.value = groupId
        loadItems(groupId)
    }

    private fun loadItems(groupId: String) {
        itemsListener?.let {
            db.getReference("Groups/${_selectedGroupId.value}/bucketList").removeEventListener(it)
        }

        itemsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bucketItems = snapshot.children.mapNotNull { it.getValue(BucketItem::class.java) }
                _items.value = bucketItems
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        
        db.getReference("Groups/$groupId/bucketList").addValueEventListener(itemsListener!!)
    }

    fun addItem(title: String, category: String) {
        val groupId = _selectedGroupId.value
        if (groupId.isBlank()) return

        viewModelScope.launch {
            try {
                val itemRef = db.getReference("Groups/$groupId/bucketList").push()
                val itemId = itemRef.key ?: return@launch
                val newItem = BucketItem(itemId = itemId, title = title, category = category)
                itemRef.setValue(newItem).await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        itemsListener?.let {
            db.getReference("Groups/${_selectedGroupId.value}/bucketList").removeEventListener(it)
        }
    }
}
