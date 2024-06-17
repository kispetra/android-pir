package com.example.pir.repository

import com.example.pir.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserProfile(): User? {
        val userId = auth.currentUser?.uid ?: return null
        val documentSnapshot = db.collection("users").document(userId).get().await()
        return documentSnapshot.toObject(User::class.java)
    }

    suspend fun updateUserProfile(userProfile: User) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).set(userProfile).await()
    }
}
