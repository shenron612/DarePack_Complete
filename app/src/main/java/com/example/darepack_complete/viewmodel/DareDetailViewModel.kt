package com.example.darepack_complete.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.darepack_complete.models.DarePackModel
import com.example.darepack_complete.models.ProofModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class DareDetailState {
    object Idle : DareDetailState()
    object Loading : DareDetailState()
    object Success : DareDetailState()
    object Uploading : DareDetailState()
    data class Error(val message: String) : DareDetailState()
}

class DareDetailViewModel : ViewModel() {
    private val _state = MutableStateFlow<DareDetailState>(DareDetailState.Idle)
    val state: StateFlow<DareDetailState> = _state

    private val _dare = MutableStateFlow<DarePackModel?>(null)
    val dare: StateFlow<DarePackModel?> = _dare

    private val _proof = MutableStateFlow<ProofModel?>(null)
    val proof: StateFlow<ProofModel?> = _proof

    fun load(dareId: String) {
        // TODO: load dare detail
    }

    fun completeDare(dareId: String, uri: Uri, caption: String) {
        // TODO: complete dare logic
    }

    fun addReaction(proofId: String, emoji: String) {
        // TODO: add reaction logic
    }
}
