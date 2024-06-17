package com.example.pir.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pir.model.User
import com.example.pir.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun fetchUserData() {
        viewModelScope.launch {
            val userData = userRepository.getUserData()
            _user.value = userData
        }
    }
}
