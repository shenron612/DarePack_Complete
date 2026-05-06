package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.DarePackModel
import com.example.darepack_complete.models.MemoryItem
import com.example.darepack_complete.models.ProofModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MemoriesViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private val _memories = MutableStateFlow<List<MemoryItem>>(emptyList())
    val memories: StateFlow<List<MemoryItem>> = _memories

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadMemories()
    }

    private fun loadMemories() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _loading.value = true
            try {
                // Get all dares where user was involved and status is completed
                val snapshot = db.getReference("Dares").get().await()
                val completedDares = snapshot.children.mapNotNull { it.getValue(DarePackModel::class.java) }
                    .filter { (it.daredTo == userId || it.daredBy == userId) && it.status == "completed" }

                val items = completedDares.map { dare ->
                    val proofSnapshot = db.getReference("Proofs/${dare.dareId}").get().await()
                    MemoryItem(
                        memoryId = dare.dareId,
                        dare = dare,
                        proof = proofSnapshot.getValue(ProofModel::class.java)
                    )
                }
                _memories.value = items
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }
}
