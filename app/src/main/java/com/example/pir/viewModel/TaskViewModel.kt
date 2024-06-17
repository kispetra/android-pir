package com.example.pir.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pir.model.Task
import com.example.pir.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    init {
        fetchTasks()
    }

    fun fetchTasks() {
        viewModelScope.launch {
            val tasksLiveData = withContext(Dispatchers.IO) {
                repository.getTasks()
            }
            tasksLiveData.observeForever { fetchedTasks ->
                _tasks.postValue(fetchedTasks)
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
            fetchTasks()
        }
    }

    fun updateTask(task: Task) {
        if (task.id.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateTask(task)
                fetchTasks()
            }
        } else {
            Log.e("TaskViewModel", "Task ID is empty in updateTask")
        }
    }

    fun deleteTask(task: Task) {
        if (task.id.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteTask(task.id)
                fetchTasks()
            }
        } else {
            Log.e("TaskViewModel", "Task ID is empty in deleteTask")
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeListener()
    }
}

