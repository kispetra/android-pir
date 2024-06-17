package com.example.pir.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pir.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private val tasksLiveData = MutableLiveData<List<Task>>()

    init {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            listenerRegistration = db.collection("users").document(userId).collection("tasks")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        tasksLiveData.value = emptyList()
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val tasks = snapshot.documents.mapNotNull { document ->
                            document.toObject(Task::class.java)?.apply { id = document.id }
                        }
                        tasksLiveData.value = tasks
                    } else {
                        tasksLiveData.value = emptyList()
                    }
                }
        }
    }


    suspend fun addTask(task: Task) {
        val userId = auth.currentUser?.uid ?: return
        val taskRef = db.collection("users").document(userId).collection("tasks").document()
        task.id = taskRef.id
        taskRef.set(task).await()
    }


    suspend fun updateTask(task: Task) {
        val userId = auth.currentUser?.uid ?: return
        if (task.id.isNotEmpty()) {
            try {
                db.collection("users").document(userId).collection("tasks").document(task.id).set(task).await()
                Log.d("TaskRepository", "Task updated successfully")
            } catch (e: Exception) {
                Log.e("TaskRepository", "Error updating task", e)
            }
        } else {
            Log.e("TaskRepository", "Task ID is empty")
        }
    }


    suspend fun deleteTask(taskId: String) {
        val userId = auth.currentUser?.uid ?: return
        if (taskId.isNotEmpty()) {
            try {
                Log.d("TaskRepository", "Deleting task with ID: $taskId")
                db.collection("users").document(userId).collection("tasks").document(taskId).delete().await()
                Log.d("TaskRepository", "Task deleted successfully")
            } catch (e: Exception) {
                Log.e("TaskRepository", "Error deleting task", e)
            }
        } else {
            Log.e("TaskRepository", "Task ID is empty")
        }
    }

    fun getTasks(): LiveData<List<Task>> {
        return tasksLiveData
    }

    suspend fun getTaskById(taskId: String): Task? {
        val userId = auth.currentUser?.uid ?: return null
        val documentSnapshot = db.collection("users").document(userId).collection("tasks").document(taskId).get().await()
        return documentSnapshot.toObject(Task::class.java)
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}
