package com.example.googlemapsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googlemapsapp.data.Repository
import com.example.googlemapsapp.models.Routes
import com.example.googlemapsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {
    sealed class DirectionsEvent{
        class Success(val resultText: List<Routes>?): DirectionsEvent()
        class Failure(val errorText: String?): DirectionsEvent()
        object Loading: DirectionsEvent()
        object Empty: DirectionsEvent()
    }

    private val _directions = MutableStateFlow<DirectionsEvent>(DirectionsEvent.Empty)
    val directions: StateFlow<DirectionsEvent> = _directions

    fun getDirections(sensor: Boolean = false, mode: String = "WALKING", alternatives: Boolean = true, key: String = "",
                      origin: String = "", destination: String = ""){
        getDirectionsSafeCall(sensor, mode, alternatives, key, origin, destination)
    }

    private fun getDirectionsSafeCall(sensor: Boolean = false, mode: String = "WALKING", alternatives: Boolean = true,
                                      key: String ="", origin: String = "", destination: String = ""){
        viewModelScope.launch{
            _directions.value = DirectionsEvent.Loading

            when(val response = repository.remote.getDirections(sensor, mode, alternatives, key, origin, destination)){
                is Resource.Error -> {
                    _directions.value = DirectionsEvent.Failure(response.message)
                }

                is Resource.Success -> {
                    _directions.value = DirectionsEvent.Success(response.data?.routes)
                }
            }
        }

    }
}