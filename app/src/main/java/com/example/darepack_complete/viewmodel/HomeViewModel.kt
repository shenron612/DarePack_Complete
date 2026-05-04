package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import com.example.darepack_complete.models.DarePackModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private val _pendingDares = MutableStateFlow<List<DarePackModel>>(emptyList())
    val pendingDares: StateFlow<List<DarePackModel>> = _pendingDares

    private val _sentDares = MutableStateFlow<List<DarePackModel>>(emptyList())
    val sentDares: StateFlow<List<DarePackModel>> = _sentDares

    private var pendingListener: ValueEventListener? = null
    private var sentListener: ValueEventListener? = null

    init {
        loadDares()
    }

    private fun loadDares() {
        val userId = auth.currentUser?.uid ?: return
        
        pendingListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dares = snapshot.children.mapNotNull { it.getValue(DarePackModel::class.java) }
                _pendingDares.value = dares.filter { it.daredTo == userId && it.status == "pending" }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        db.getReference("Dares").addValueEventListener(pendingListener!!)

        sentListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dares = snapshot.children.mapNotNull { it.getValue(DarePackModel::class.java) }
                _sentDares.value = dares.filter { it.daredBy == userId }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        db.getReference("Dares").addValueEventListener(sentListener!!)
    }

    override fun onCleared() {
        super.onCleared()
        pendingListener?.let { db.getReference("Dares").removeEventListener(it) }
        sentListener?.let { db.getReference("Dares").removeEventListener(it) }
    }
}
