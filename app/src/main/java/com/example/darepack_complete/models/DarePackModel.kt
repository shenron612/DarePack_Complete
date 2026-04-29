package com.example.darepack_complete.models

import com.google.firebase.Timestamp

data class DarePackModel(
    var dareId: String = "",
    var title: String = "",
    var daredByName: String = "",
    var daredToName: String = "",
    var daredTo: String = "",
    var status: String = "pending",
    var deadline: Timestamp = Timestamp.now()
)
