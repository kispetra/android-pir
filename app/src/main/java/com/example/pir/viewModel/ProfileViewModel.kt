package com.example.pir.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pir.model.User
import com.example.pir.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel() : ViewModel() {

    private val repository = ProfileRepository()
    private val _userProfile = MutableLiveData<User?>()
    val userProfile: MutableLiveData<User?> = _userProfile

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val userProfile = repository.getUserProfile()
            _userProfile.postValue(userProfile)
        }
    }

    fun updateUserProfile(brideName: String, groomName: String, weddingDate: String) {
        val userProfile = User(brideName, groomName, weddingDate)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserProfile(userProfile)
            _userProfile.postValue(userProfile)
        }
    }
}
