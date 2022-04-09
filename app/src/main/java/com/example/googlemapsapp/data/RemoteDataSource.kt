package com.example.googlemapsapp.data

import com.example.googlemapsapp.DirectionsApi
import com.example.googlemapsapp.models.DirectionsResponse
import com.example.googlemapsapp.util.Resource
import java.lang.Exception
import javax.inject.Inject

class RemoteDataSource@Inject constructor(private val directionsApi: DirectionsApi){
    suspend fun getDirections(mode: String = "DRIVING", alternatives: Boolean = true, key: String = "",
                              origin: String = "", destination: String = ""): Resource<DirectionsResponse> {
        return try{
            val response = directionsApi.getDirectionsBetween(mode, alternatives, key, origin, destination)
            val result = response.body()

            if(response.isSuccessful && result != null){
                Resource.Success(result)
            } else{
                Resource.Error(response.message())
            }
        }
        catch (e: Exception){
            //if its null, then "an error occurred"
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}