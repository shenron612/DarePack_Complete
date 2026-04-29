package com.example.darepack_complete.models

data class UserModel(
    var userId: String = "",
    var username: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var totalCompleted: Int = 0,
    var photoUrl: String = ""
)
