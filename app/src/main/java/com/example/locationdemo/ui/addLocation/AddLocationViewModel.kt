package com.example.locationdemo.ui.addLocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationdemo.data.local.Location
import com.example.locationdemo.data.remote.SearchPlaceResponse
import com.example.locationdemo.repository.LocationRepository
import com.example.locationdemo.utils.Constant.PLACE_API_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {
    private val _searchResults = MutableLiveData<SearchPlaceResponse>()
    val searchResults: LiveData<SearchPlaceResponse> get() = _searchResults

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun searchPlaces(query: String) {
        viewModelScope.launch {
            try {
                val response = repository.searchPlace(query, PLACE_API_KEY)
                if (response.isSuccessful) {
                    _searchResults.postValue(response.body())
                } else {
                    _error.postValue("Failed to fetch places.")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
            }
        }
    }

    fun addLocation(location: Location) {
        viewModelScope.launch {
            repository.addLocation(location)
        }
    }

    fun updateLocation(id: Long, name: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.updateLocationById(id, name, latitude, longitude)
        }
    }
}