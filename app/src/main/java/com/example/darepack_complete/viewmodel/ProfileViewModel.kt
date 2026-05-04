package com.example.darepack_complete.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.darepack_complete.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _uploading = MutableStateFlow(false)
    val uploading: StateFlow<Boolean> = _uploading

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.getReference("Users/$userId").get().await()
                _user.value = snapshot.getValue(UserModel::class.java)
            } catch (e: Exception) {}
        }
    }

    fun updateProfileImage(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uploading.value = true
            try {
                val ref = storage.getReference("Avatars/$userId.jpg")
                ref.putFile(uri).await()
                val url = ref.downloadUrl.await().toString()
                
                db.getReference("Users/$userId/photoUrl").setValue(url).await()
                loadUserProfile()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uploading.value = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
