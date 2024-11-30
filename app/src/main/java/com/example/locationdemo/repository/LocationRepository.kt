package com.example.locationdemo.repository

import com.example.locationdemo.data.local.Location
import com.example.locationdemo.data.local.LocationDao
import com.example.locationdemo.data.remote.DirectionsResponse
import com.example.locationdemo.data.remote.SearchPlaceResponse
import com.example.locationdemo.network.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val googlePlacesApi: ApiService
) {
    suspend fun addLocation(location: Location) = locationDao.insert(location)

    suspend fun updateLocationById(id: Long, name: String, latitude: Double, longitude: Double) {
        locationDao.updateLocationById(id, name, latitude, longitude)
    }

    suspend fun deleteLocation(location: Location) = locationDao.delete(location)

    suspend fun getAllLocations() = locationDao.getAllLocations()

    suspend fun searchPlace(input: String, apiKey: String): Response<SearchPlaceResponse> {
        return googlePlacesApi.searchPlaces(input, apiKey)
    }

    suspend fun getDirections(
        origin: String,
        destination: String,
        waypoints: String,
        apiKey: String
    ): Response<DirectionsResponse> {
        return googlePlacesApi.getDirections(origin, destination, waypoints, apiKey)
    }
}