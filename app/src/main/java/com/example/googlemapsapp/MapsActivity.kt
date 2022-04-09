package com.example.googlemapsapp

import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.example.googlemapsapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val podgorica = LatLng(42.43, 19.25)

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(podgorica, 14.0F))

        var origin: Marker?
        var destination: Marker?
        var counter = 0

        binding.btnMakeRoute.setOnClickListener{
            binding.btnMakeRoute.visibility = INVISIBLE

            mMap.setOnMapClickListener { latLng -> // Creating a marker
                if(counter == 0)
                    mMap.clear()

                val markerOptions = MarkerOptions()

                // Setting the position for the marker
                markerOptions.position(latLng)

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)

                // Clears the previously touched position
                //googleMap.clear()

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

                // Placing a marker on the touched position
                counter++
                if(counter == 1)
                    origin = googleMap.addMarker(markerOptions)

                else if(counter == 2){
                    destination = googleMap.addMarker(markerOptions)
                    counter = 0
                    binding.btnMakeRoute.visibility = VISIBLE
                }
            }
        }

    }
}