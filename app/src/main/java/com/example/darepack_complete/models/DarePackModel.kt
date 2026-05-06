package com.example.darepack_complete.models

data class DarePackModel(
    var dareId: String = "",
    var title: String = "",
    var daredBy: String = "",
    var daredByName: String = "",
    var daredTo: String = "",
    var daredToName: String = "",
    var status: String = "pending",
    var deadline: Any? = 0L
) {
    val deadlineLong: Long
        get() = when (val d = deadline) {
            is Long -> d
            is Double -> d.toLong()
            is Map<*, *> -> {
                // If it's a ServerValue.TIMESTAMP or Firestore Timestamp
                (d["seconds"] as? Long)?.let { it * 1000 }
                    ?: (d["_seconds"] as? Long)?.let { it * 1000 }
                    ?: 0L
            }
            else -> 0L
        }
}
