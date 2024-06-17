package com.example.pir.repository

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pir.model.Guest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class GuestRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private val guestLiveData = MutableLiveData<List<Guest>>()

    init {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            listenerRegistration = db.collection("users").document(userId).collection("guests")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", e)
                        guestLiveData.value = emptyList()
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val guests = snapshot.documents.mapNotNull { document ->
                            document.toObject(Guest::class.java)?.apply { id = document.id }
                        }
                        guestLiveData.value = guests
                    } else {
                        guestLiveData.value = emptyList()
                    }
                }
        }
    }

    fun getGuests(category: String): LiveData<List<Guest>> {
        val userId = auth.currentUser?.uid ?: return MutableLiveData(emptyList())
        val guestsLiveData = MutableLiveData<List<Guest>>()

        db.collection("users").document(userId).collection("guests")
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("GuestRepository", "Error fetching guests: ${e.message}")
                    guestsLiveData.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val guests = snapshot.documents.mapNotNull { it.toObject(Guest::class.java)?.apply { id = it.id } }
                    Log.d("GuestRepository", "Fetched guests: $guests")
                    guestsLiveData.value = guests
                } else {
                    Log.d("GuestRepository", "No guests found")
                    guestsLiveData.value = emptyList()
                }
            }

        return guestsLiveData
    }

    fun searchGuests(category: String, searchText: String): LiveData<List<Guest>> {
        val userId = auth.currentUser?.uid ?: return MutableLiveData(emptyList())
        val guestsLiveData = MutableLiveData<List<Guest>>()

        db.collection("users").document(userId).collection("guests")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val guests = snapshot.documents.mapNotNull { it.toObject(Guest::class.java)?.apply { id = it.id } }
                        .filter {
                            it.firstName.toLowerCase().contains(searchText) ||
                                    it.lastName.toLowerCase().contains(searchText)
                        }
                    Log.d("GuestRepository", "Searched guests: $guests")
                    guestsLiveData.value = guests
                } else {
                    Log.d("GuestRepository", "No guests found for search")
                    guestsLiveData.value = emptyList()
                }
            }
            .addOnFailureListener { e ->
                Log.e("GuestRepository", "Error searching guests: ${e.message}")
                guestsLiveData.value = emptyList()
            }

        return guestsLiveData
    }

    suspend fun addGuest(guest: Guest) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("guests").add(guest).await()
        Log.d("GuestRepository", "Guest added: $guest")
    }

    suspend fun updateGuest(guest: Guest) {
        val userId = auth.currentUser?.uid ?: return
        guest.id?.let { id ->
            db.collection("users").document(userId).collection("guests").document(id)
                .set(guest).await()
            Log.d("GuestRepository", "Guest updated: $guest")
        } ?: Log.e("GuestRepository", "Error updating guest: ID is null")
    }

    suspend fun deleteGuest(guestId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("guests").document(guestId)
            .delete().await()
        Log.d("GuestRepository", "Guest deleted: $guestId")
    }
}
