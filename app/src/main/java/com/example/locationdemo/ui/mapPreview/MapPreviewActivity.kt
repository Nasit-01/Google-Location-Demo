package com.example.locationdemo.ui.mapPreview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.locationdemo.R
import com.example.locationdemo.data.local.Location
import com.example.locationdemo.databinding.ActivityMapPreviewBinding
import com.example.locationdemo.ui.locationList.LocationListViewModel
import com.example.locationdemo.utils.Util
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapPreviewActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityMapPreviewBinding
    private lateinit var map: GoogleMap
    private lateinit var locationListViewModel: LocationListViewModel
    private lateinit var viewModel: MapPreviewViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapPreviewBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationListViewModel = ViewModelProvider(this).get(LocationListViewModel::class.java)
        viewModel = ViewModelProvider(this).get(MapPreviewViewModel::class.java)

        initView()
        subscribeObserver()

    }

    fun initView() {
        Util.initToolBarTitle(getString(R.string.map_view), this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationListViewModel.getAllLocation()

        binding.toolbar.imgBackAerrow.setOnClickListener {
            finish()
        }
    }

    fun subscribeObserver() {
        locationListViewModel.getAllLocationResponse.observe(this) { response ->
            drawMarkersAndPath(response as ArrayList<Location>)
        }

        viewModel.getDirectionResponse.observe(this) { response ->
            val directionsResponse = response.body()
            directionsResponse?.routes?.firstOrNull()?.overview_polyline?.points?.let { polyline ->
                val decodedPath = decodePolyline(polyline)
                drawPolylineOnMap(decodedPath)
                adjustCameraToFitPath(decodedPath)
            }

        }
    }

    private fun drawMarkersAndPath(locations: ArrayList<Location>) {
        if (locations.isEmpty()) return

        val latLngList = locations.map { LatLng(it.latitude, it.longitude) }

        // Display the markers on the map
        latLngList.forEach { latLng ->
            map.addMarker(MarkerOptions().position(latLng))
        }

        // Use Retrofit to get the directions route
        val origin = "${latLngList.first().latitude},${latLngList.first().longitude}"
        val destination = "${latLngList.last().latitude},${latLngList.last().longitude}"
        val waypoints =
            latLngList.drop(1).dropLast(1).joinToString("|") { "${it.latitude},${it.longitude}" }

        // Call the Directions API
        viewModel.getDirections(origin, destination, waypoints)
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val polyline = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) result.shr(1).inv() else result.shr(1)
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) result.shr(1).inv() else result.shr(1)
            lng += dlng

            polyline.add(LatLng(lat / 1E5, lng / 1E5))
        }

        return polyline
    }

    private fun drawPolylineOnMap(path: List<LatLng>) {
        val polylineOptions = PolylineOptions().addAll(path)
        map.addPolyline(polylineOptions)
    }

    // Adjust the camera to fit all markers and the polyline
    private fun adjustCameraToFitPath(path: List<LatLng>) {
        val bounds = LatLngBounds.builder()

        // Include all the LatLng points in the bounds
        path.forEach { bounds.include(it) }

        // Create a camera update to fit the bounds with a padding of 100px
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100)

        // Move the camera to fit the polyline and markers
        map.moveCamera(cameraUpdate)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
    }
}