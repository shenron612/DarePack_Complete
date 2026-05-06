package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.BucketItem
import com.example.darepack_complete.models.DarePackModel
import com.example.darepack_complete.models.Group
import com.example.darepack_complete.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

sealed class SendDareState {
    object Idle : SendDareState()
    object Loading : SendDareState()
    object Success : SendDareState()
    data class Error(val message: String) : SendDareState()
}

class SendDareViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private val _state = MutableStateFlow<SendDareState>(SendDareState.Idle)
    val state: StateFlow<SendDareState> = _state

    private val _item = MutableStateFlow<BucketItem?>(null)
    val item: StateFlow<BucketItem?> = _item

    private val _members = MutableStateFlow<List<UserModel>>(emptyList())
    val members: StateFlow<List<UserModel>> = _members

    private var currentGroupId: String = ""

    fun load(itemId: String, groupId: String) {
        currentGroupId = groupId
        viewModelScope.launch {
            try {
                // Load item
                val itemSnapshot = db.getReference("Groups/$groupId/bucketList/$itemId").get().await()
                _item.value = itemSnapshot.getValue(BucketItem::class.java)

                // Load group members
                val groupSnapshot = db.getReference("Groups/$groupId").get().await()
                val group = groupSnapshot.getValue(Group::class.java)
                
                val memberIds = group?.members ?: emptyList()
                val currentUserId = auth.currentUser?.uid
                
                val fetchedMembers = memberIds.filter { it != currentUserId }.mapNotNull { mid ->
                    db.getReference("Users/$mid").get().await().getValue(UserModel::class.java)
                }
                _members.value = fetchedMembers
            } catch (e: Exception) {}
        }
    }

    fun sendDare(target: UserModel, deadlineMillis: Long) {
        val currentUserId = auth.currentUser?.uid ?: return
        val item = _item.value ?: return

        viewModelScope.launch {
            _state.value = SendDareState.Loading
            try {
                // Get current user name for the dare
                val currentUserSnapshot = db.getReference("Users/$currentUserId").get().await()
                val currentUser = currentUserSnapshot.getValue(UserModel::class.java)

                val dareRef = db.getReference("Dares").push()
                val dareId = dareRef.key ?: throw Exception("Failed to generate Dare ID")

                val dare = DarePackModel(
                    dareId = dareId,
                    title = item.title,
                    daredBy = currentUserId,
                    daredByName = currentUser?.name ?: "Someone",
                    daredTo = target.userId,
                    daredToName = target.name,
                    status = "pending",
                    deadline = deadlineMillis
                )

                dareRef.setValue(dare).await()
                _state.value = SendDareState.Success
            } catch (e: Exception) {
                _state.value = SendDareState.Error(e.message ?: "Failed to send dare")
            }
        }
    }
}
