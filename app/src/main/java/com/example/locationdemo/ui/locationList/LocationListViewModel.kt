package com.example.locationdemo.ui.locationList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationdemo.data.local.Location
import com.example.locationdemo.data.remote.DirectionsResponse
import com.example.locationdemo.data.remote.SearchPlaceResponse
import com.example.locationdemo.repository.LocationRepository
import com.example.locationdemo.utils.Constant.PLACE_API_KEY
import com.example.locationdemo.utils.Util.getDistanceTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    private val _allLocationResults = MutableLiveData<List<Location>>()
    val getAllLocationResponse: LiveData<List<Location>> get() = _allLocationResults

    fun getAllLocation() {
        viewModelScope.launch {
            val locations = repository.getAllLocations()
            _allLocationResults.postValue(locations)
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            repository.deleteLocation(location)
        }
    }

    private val _sortOrder = MutableLiveData<String>("ASC")  // ASC or DESC

    // Assuming you have a method to get the user's current location
    private val currentLat = 37.4219999
    private val currentLon = -122.0840575

    // Method to fetch locations based on selected sorting order
    fun loadLocations() {
        viewModelScope.launch {
            // Fetch all locations from the repository
            val allLocations = repository.getAllLocations()

            // Sort locations by distance and selected order (ASC or DESC)
            val sortedLocations = when (_sortOrder.value) {
                "ASC" -> allLocations.sortedBy {
                    getDistanceTo(
                        it.latitude,
                        it.longitude,
                        currentLat,
                        currentLon
                    )
                }

                "DESC" -> allLocations.sortedByDescending {
                    getDistanceTo(
                        it.latitude,
                        it.longitude,
                        currentLat,
                        currentLon
                    )
                }

                else -> allLocations
            }

            // Update LiveData with the sorted list
            _allLocationResults.postValue(sortedLocations)
        }
    }

    // Method to update the sorting order based on radio button selection
    fun setSortOrder(sortOrder: String) {
        _sortOrder.value = sortOrder
    }
}