package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import com.example.darepack_complete.models.DarePackModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _pendingDares = MutableStateFlow<List<DarePackModel>>(emptyList())
    val pendingDares: StateFlow<List<DarePackModel>> = _pendingDares

    private val _sentDares = MutableStateFlow<List<DarePackModel>>(emptyList())
    val sentDares: StateFlow<List<DarePackModel>> = _sentDares
}
