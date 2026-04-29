package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import com.example.darepack_complete.models.BucketItem
import com.example.darepack_complete.models.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class SendDareState {
    object Idle : SendDareState()
    object Loading : SendDareState()
    object Success : SendDareState()
    data class Error(val message: String) : SendDareState()
}

class SendDareViewModel : ViewModel() {
    private val _state = MutableStateFlow<SendDareState>(SendDareState.Idle)
    val state: StateFlow<SendDareState> = _state

    private val _item = MutableStateFlow<BucketItem?>(null)
    val item: StateFlow<BucketItem?> = _item

    private val _members = MutableStateFlow<List<UserModel>>(emptyList())
    val members: StateFlow<List<UserModel>> = _members

    fun load(itemId: String, groupId: String) {
        // TODO: load data
    }

    fun sendDare(target: UserModel, deadlineMillis: Long) {
        // TODO: send dare
    }
}
