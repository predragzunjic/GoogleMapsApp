package com.example.googlemapsapp

import com.example.googlemapsapp.models.DirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface DirectionsApi {
    @GET("directions/json")
    suspend fun getDirectionsBetween(
        @Query("mode") mode: String,
        @Query("alternatives") alternatives: Boolean,
        @Query("key") key: String,
        @Query("origin") origin: String,
        @Query("destination") Destination: String
    ): Response<DirectionsResponse>

}