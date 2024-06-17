package com.example.pir.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pir.model.Guest
import com.example.pir.repository.GuestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuestViewModel : ViewModel() {

    private val repository = GuestRepository()
    private val _guests = MutableLiveData<List<Guest>>()
    val guests: LiveData<List<Guest>> = _guests

    fun fetchGuests(category: String) {
        viewModelScope.launch {
            val guestsLiveData = withContext(Dispatchers.IO) {
                repository.getGuests(category)
            }
            guestsLiveData.observeForever { fetchedGuests ->
                _guests.postValue(fetchedGuests)
                Log.d("GuestViewModel", "Fetched guests: $fetchedGuests")
            }
        }
    }

    fun searchGuests(category: String, searchText: String) {
        viewModelScope.launch {
            val guestsLiveData = withContext(Dispatchers.IO) {
                repository.searchGuests(category, searchText)
            }
            guestsLiveData.observeForever { fetchedGuests ->
                _guests.postValue(fetchedGuests)
                Log.d("GuestViewModel", "Searched guests: $fetchedGuests")
            }
        }
    }

    fun addGuest(guest: Guest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addGuest(guest)
            fetchGuests(guest.category)
            Log.d("GuestViewModel", "Guest added: $guest")
        }
    }

    fun updateGuest(guest: Guest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGuest(guest)
            fetchGuests(guest.category)
            Log.d("GuestViewModel", "Guest updated: $guest")
        }
    }

    fun deleteGuest(guestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteGuest(guestId)
            fetchGuests("") // Adjust based on your needs
            Log.d("GuestViewModel", "Guest deleted: $guestId")
        }
    }
}
