package com.example.googlemapsapp.models

data class Routes(
    var legs: List<Legs>?,

    val overview_polyline: OverviewPolyline

)