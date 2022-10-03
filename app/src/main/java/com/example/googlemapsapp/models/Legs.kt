package com.example.googlemapsapp.models

data class Legs(
    var distance: Distance?,

    var duration: Duration?,

    var end_adress: String?,

    var end_location: Location?,

    var start_adress: String?,

    var start_location: Location?,

    var steps: List<Steps>?
)