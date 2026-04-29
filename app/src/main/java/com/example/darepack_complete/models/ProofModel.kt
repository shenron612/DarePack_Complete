package com.example.darepack_complete.models

data class ProofModel(
    val proofId: String = "",
    val photoUrl: String = "",
    val caption: String = "",
    val reactions: Map<String, String> = emptyMap()
)
