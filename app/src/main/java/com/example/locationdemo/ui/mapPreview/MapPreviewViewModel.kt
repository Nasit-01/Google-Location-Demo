package com.example.locationdemo.ui.mapPreview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationdemo.data.remote.DirectionsResponse
import com.example.locationdemo.repository.LocationRepository
import com.example.locationdemo.utils.Constant.PLACE_API_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MapPreviewViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {
    private val _getDirectionResults = MutableLiveData<Response<DirectionsResponse>>()
    val getDirectionResponse: LiveData<Response<DirectionsResponse>> get() = _getDirectionResults

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getDirections(origin: String, destination: String, waypoints: String) {
        viewModelScope.launch {
            try {
                val response =
                    repository.getDirections(origin, destination, waypoints, PLACE_API_KEY)
                if (response.isSuccessful) {
                    _getDirectionResults.postValue(response)
                } else {
                    _error.postValue("Failed to fetch places.")
                }
            } catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
            }
        }
    }
}