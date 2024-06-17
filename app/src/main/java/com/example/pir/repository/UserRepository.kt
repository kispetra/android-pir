package com.example.pir.repository

import com.example.pir.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserData(): User? {
        val userId = auth.currentUser?.uid ?: return null
        val documentSnapshot = db.collection("users").document(userId).get().await()
        return documentSnapshot.toObject(User::class.java)
    }
}
