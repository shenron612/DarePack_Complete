package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import com.example.darepack_complete.models.MemoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MemoriesViewModel : ViewModel() {
    private val _memories = MutableStateFlow<List<MemoryItem>>(emptyList())
    val memories: StateFlow<List<MemoryItem>> = _memories

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
}
