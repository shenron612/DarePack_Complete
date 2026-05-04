package com.example.darepack_complete.models

import com.google.firebase.Timestamp

data class DarePackModel(
    var dareId: String = "",
    var title: String = "",
    var daredBy: String = "",
    var daredByName: String = "",
    var daredTo: String = "",
    var daredToName: String = "",
    var status: String = "pending",
    var deadline: Timestamp = Timestamp.now()
)
