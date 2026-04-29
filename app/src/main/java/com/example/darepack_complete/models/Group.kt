package com.example.darepack_complete.models

data class Group(
    val groupId: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val members: List<String> = emptyList()
)
