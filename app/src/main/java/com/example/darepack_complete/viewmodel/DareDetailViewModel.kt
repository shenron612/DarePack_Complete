package com.example.darepack_complete.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.DarePackModel
import com.example.darepack_complete.models.ProofModel
import com.example.darepack_complete.utils.CloudinaryHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class DareDetailState {
    object Idle : DareDetailState()
    object Loading : DareDetailState()
    object Success : DareDetailState()
    object Uploading : DareDetailState()
    data class Error(val message: String) : DareDetailState()
}

class DareDetailViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private val _state = MutableStateFlow<DareDetailState>(DareDetailState.Idle)
    val state: StateFlow<DareDetailState> = _state

    private val _dare = MutableStateFlow<DarePackModel?>(null)
    val dare: StateFlow<DarePackModel?> = _dare

    private val _proof = MutableStateFlow<ProofModel?>(null)
    val proof: StateFlow<ProofModel?> = _proof

    fun load(dareId: String) {
        viewModelScope.launch {
            _state.value = DareDetailState.Loading
            try {
                val snapshot = db.getReference("Dares/$dareId").get().await()
                val d = snapshot.getValue(DarePackModel::class.java)
                _dare.value = d
                
                if (d?.status == "completed") {
                    val pSnapshot = db.getReference("Proofs/$dareId").get().await()
                    _proof.value = pSnapshot.getValue(ProofModel::class.java)
                }
                _state.value = DareDetailState.Idle
            } catch (e: Exception) {
                _state.value = DareDetailState.Error(e.message ?: "Failed to load")
            }
        }
    }

    fun completeDare(context: Context, dareId: String, uri: Uri, caption: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _state.value = DareDetailState.Uploading
            try {
                val url = CloudinaryHelper.uploadImage(context, uri)

                val proof = ProofModel(
                    proofId = dareId,
                    photoUrl = url,
                    caption = caption
                )

                // Update Dare status and Proof in parallel-ish
                db.getReference("Dares/$dareId/status").setValue("completed").await()
                db.getReference("Proofs/$dareId").setValue(proof).await()
                
                // Update user stats
                val userRef = db.getReference("Users/$userId/totalCompleted")
                val current = userRef.get().await().getValue(Int::class.java) ?: 0
                userRef.setValue(current + 1).await()

                _state.value = DareDetailState.Success
                load(dareId)
            } catch (e: Exception) {
                _state.value = DareDetailState.Error(e.message ?: "Upload failed")
            }
        }
    }

    fun addReaction(proofId: String, emoji: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                db.getReference("Proofs/$proofId/reactions").child(userId).setValue(emoji).await()
                // Reload proof to show reaction
                val pSnapshot = db.getReference("Proofs/$proofId").get().await()
                _proof.value = pSnapshot.getValue(ProofModel::class.java)
            } catch (e: Exception) {}
        }
    }
}
