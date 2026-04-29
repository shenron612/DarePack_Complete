package com.example.darepack_complete.models

data class MemoryItem(
    val memoryId: String = "",
    val dare: DarePackModel = DarePackModel(),
    val proof: ProofModel? = null
)
