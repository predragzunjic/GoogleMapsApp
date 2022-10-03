package com.example.googlemapsapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.googlemapsapp.databinding.ActivityMapsBinding
import com.example.googlemapsapp.databinding.LoadingRouteBinding
import com.example.googlemapsapp.models.Legs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var binding2: LoadingRouteBinding
    private val mapsViewModel: MapsViewModel by viewModels()
    private lateinit var listOfMarkers: MutableList<Marker?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding2 = LoadingRouteBinding.inflate(layoutInflater)

        changeBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)

        listOfMarkers = mutableListOf()
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

        initView()
    }

    private fun initView(){
        var counter = 0
        var key = 0

        binding.btnMakeRoute.setOnClickListener{

            if(key == 0 && binding.btnMakeRoute.text == getString(R.string.btn_make_route)) {
                binding.btnMakeRoute.text = getString(R.string.btn_make_route_cancel)
                key = 1
            }

            else if(key == 1 && binding.btnMakeRoute.text == getString(R.string.btn_make_route_cancel)){
                binding.btnMakeRoute.text = getString(R.string.btn_make_route)
                mMap.clear()
                listOfMarkers.clear()
                changeBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)

                key = 0
                counter = 0
            }

            mMap.setOnMapClickListener { latLng -> // Creating a marker
                val markerOptions = MarkerOptions()

                // Setting the position for the marker
                markerOptions.position(latLng)

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude.toString())

                // Clears the previously touched position
                //googleMap.clear()

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

                // Placing a marker on the touched position
                counter++
                listOfMarkers.add(mMap.addMarker(markerOptions))

                if(counter == 2){
                    counter = 0
                    getDirections(listOfMarkers)
                }
            }
        }
    }

    private fun getDirections(listOfMarkers: MutableList<Marker?>){
        mapsViewModel.getDirections(false, "WALKING", true, BuildConfig.API_KEY,
                "${listOfMarkers[0]?.title?.substringBefore(':')}," +
                        "${listOfMarkers[0]?.title?.substringAfter(':')}",
                "${listOfMarkers[1]?.title?.substringBefore(':')},"
                        + "${listOfMarkers[1]?.title?.substringAfter(':')}")


        lifecycleScope.launchWhenStarted {
            mapsViewModel.directions.collect{event ->
                when(event){
                    is MapsViewModel.DirectionsEvent.Success ->{
                        setContentView(binding.root)
                        createPolyline(event.resultText?.get(0)?.overview_polyline?.points.toString())

                        changeBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
                        showBottomSheet(event.resultText?.get(0)?.legs)
                    }

                    is MapsViewModel.DirectionsEvent.Failure ->{
                        Toast.makeText(this@MapsActivity, event.errorText, Toast.LENGTH_SHORT).show()
                    }

                    is MapsViewModel.DirectionsEvent.Loading ->{
                        setContentView(binding2.root)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun createPolyline(result: String?) {
        if (!result.isNullOrEmpty()) {
            val list = PolyUtil.decode(result)
            val polylineOptions = PolylineOptions().width(5f).color(Color.RED).geodesic(true)
            for (it in list) {
                polylineOptions.add(it)
            }
            mMap.addPolyline(polylineOptions)
        }else{
            Toast.makeText(this@MapsActivity, "There is a problem with creating an interface",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun showBottomSheet(legs: List<Legs>?) {
        BottomSheetBehavior.from(binding.frameLayoutSheet).apply {
            this.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.loadingRoute.tvTimeDistance.text = legs?.get(0)?.distance?.text + " " + legs?.get(0)?.duration?.text
        }
    }

    private fun changeBottomSheetState(state: Int) {
        BottomSheetBehavior.from(binding.frameLayoutSheet).apply {
            this.state = state
        }
    }
}