package com.example.locationdemo.network

import com.example.locationdemo.data.remote.DirectionsResponse
import com.example.locationdemo.data.remote.SearchPlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("place/autocomplete/json")
    suspend fun searchPlaces(
        @Query("input") input: String,
        @Query("key") apiKey: String
    ): Response<SearchPlaceResponse>

    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String?,
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}
