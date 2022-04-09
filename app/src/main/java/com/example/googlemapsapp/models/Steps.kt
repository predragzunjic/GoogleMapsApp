package com.example.googlemapsapp.models

import android.location.Location
import com.google.android.gms.maps.model.Polyline

class Steps(
    var start_location: Location?,

    var end_location: Location?,

    var polyline: Polyline?,

    var travel_mode: String?
    )