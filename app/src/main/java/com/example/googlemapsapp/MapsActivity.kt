package com.example.googlemapsapp

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.googlemapsapp.databinding.ActivityMapsBinding
import com.example.googlemapsapp.models.Steps
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MapsViewModel by viewModels()
    private lateinit var listOfMarkers: MutableList<Marker?>
    private lateinit var KEY_API: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listOfMarkers = mutableListOf<Marker?>()
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["keyValue"]

        KEY_API = value.toString()
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

        initView(googleMap)


    }

    private fun initView(googleMap: GoogleMap){
        var counter = 0

        var origin: Marker?
        var destination: Marker?

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
                    listOfMarkers.add(googleMap.addMarker(markerOptions))

                else if(counter == 2){
                    listOfMarkers.add(googleMap.addMarker(markerOptions))
                    counter = 0
                    getDirections(listOfMarkers)
                    listOfMarkers.clear()
                    binding.btnMakeRoute.visibility = VISIBLE
                }
            }
        }
    }

    private fun getDirections(listOfMarkers: MutableList<Marker?>){
        mapsViewModel.getDirections("DRIVING", true, KEY_API, "${listOfMarkers[0]?.title?.substringBefore(':')}," +
                "${listOfMarkers[0]?.title?.substringBefore(':')}", "${listOfMarkers[1]?.title?.substringBefore(':')},"
                + "${listOfMarkers[1]?.title?.substringBefore(':')}")

        lifecycleScope.launchWhenStarted {
            mapsViewModel.directions.collect{event ->
                when(event){
                    is MapsViewModel.DirectionsEvent.Success ->{
                        createPolyline(event.resultText)
                    }

                    is MapsViewModel.DirectionsEvent.Failure ->{

                    }

                    is MapsViewModel.DirectionsEvent.Loading ->{

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun createPolyline(steps: List<Steps>?) {
        if (!steps.isNullOrEmpty()) {
            for (it in steps) {
                mMap.addPolyline(
                    PolylineOptions()
                        .add(LatLng(it.start_location?.latitude!!.toDouble(), it.start_location?.longitude!!.toDouble())
                            , LatLng(it.end_location?.latitude!!.toDouble(), it.end_location?.longitude!!.toDouble()))
                        .width(5f)
                        .color(Color.RED)
                )
            }
        }else{
            Toast.makeText(this@MapsActivity, "There is a problem with creating an interface",
                Toast.LENGTH_LONG).show()
        }
    }
}