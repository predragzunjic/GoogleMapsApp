package com.example.googlemapsapp.models

data class Steps(
    var distance: Distance?,

    var duration: Duration?,

    var start_location: Location?,

    var end_location: Location?,

    var travel_mode: String?
)