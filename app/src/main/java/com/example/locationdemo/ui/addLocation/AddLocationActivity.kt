package com.example.locationdemo.ui.addLocation

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationdemo.R
import com.example.locationdemo.data.local.Location
import com.example.locationdemo.data.remote.PredictionsItem
import com.example.locationdemo.databinding.ActivityAddLocationBinding
import com.example.locationdemo.listener.OnRecyclerItemClickListener
import com.example.locationdemo.utils.Params.INTENT_IS_FROM_UPDATE
import com.example.locationdemo.utils.Params.INTENT_LOCATION
import com.example.locationdemo.utils.Util
import com.example.locationdemo.utils.Util.hideKeyboard
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityAddLocationBinding
    private lateinit var viewModel: AddLocationViewModel
    private lateinit var searchedLocationAdapter: SearchedLocationAdapter
    private var searchedLocationList: ArrayList<PredictionsItem> = ArrayList()
    private lateinit var map: GoogleMap
    var locationName: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var isFromUpdate = false
    var locationToUpdate: Location? = null
    private var isLocationSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this).get(AddLocationViewModel::class.java)

        if (intent.hasExtra(INTENT_IS_FROM_UPDATE)) {
            isFromUpdate = intent.getBooleanExtra(INTENT_IS_FROM_UPDATE, false)
            locationToUpdate = intent.getParcelableExtra(INTENT_LOCATION)
        }
        initView()
        subscribeObserver()
    }

    fun initView() {
        Util.initToolBarTitle(
            if (isFromUpdate) getString(R.string.update_location) else getString(R.string.add_location),
            this
        )

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (isFromUpdate) {
            if (locationToUpdate != null) {
                latitude = locationToUpdate?.latitude ?: 0.0
                longitude = locationToUpdate?.latitude ?: 0.0
                locationName = locationToUpdate?.name ?: ""

                isLocationSelected=true
                binding.etSearch.setText(locationName)

            }
            binding.btnAddLocation.text = getString(R.string.update)
        }
        binding.rvSearchedLocationList.setLayoutManager(LinearLayoutManager(this))

        searchedLocationAdapter =
            SearchedLocationAdapter(searchedLocationList, object : OnRecyclerItemClickListener {
                override fun onRecyclerItemClicked(view: View?, position: Int) {
                    hideKeyboard(this@AddLocationActivity)
                    binding.etSearch.clearFocus()
                    binding.rvSearchedLocationList.visibility = View.GONE
                    locationName = searchedLocationList[position].description.toString()
                    val selectedItem = searchedLocationList[position]
                    selectedItem.placeId?.let { fetchPlaceDetails(it) }
                    binding.etSearch.setText(locationName)
                }
            })
        binding.rvSearchedLocationList.adapter = searchedLocationAdapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (!isLocationSelected) {
                    viewModel.searchPlaces(s.toString())
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        });

        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                isLocationSelected=false
            }else{
                isLocationSelected=true
            }
        }

        binding.btnAddLocation.setOnClickListener {
            if (locationName.isNotEmpty()){
                if (isFromUpdate) {
                    locationToUpdate?.let { it1 ->
                        viewModel.updateLocation(
                            locationToUpdate?.id ?: 0,
                            locationName,
                            latitude,
                            longitude
                        )
                    }
                } else {
                    viewModel.addLocation(
                        Location(
                            name = locationName,
                            latitude = latitude,
                            longitude = longitude
                        )
                    )
                }
                finish()
            }else{
                Toast.makeText(this,getString(R.string.select_location_msg),Toast.LENGTH_LONG).show()
            }
        }

        binding.toolbar.imgBackAerrow.setOnClickListener {
            finish()
        }
    }

    fun subscribeObserver() {
        viewModel.searchResults.observe(this) { response ->
            searchedLocationList.clear()
            response.predictions?.let { searchedLocationList.addAll(it) }
            searchedLocationAdapter.notifyDataSetChanged()
            if (searchedLocationList.isNotEmpty()) {
                binding.rvSearchedLocationList.visibility = View.VISIBLE
            } else {
                binding.rvSearchedLocationList.visibility = View.GONE
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (isFromUpdate){
            if (latitude != 0.0 && longitude != 0.0) {
                // Move camera to the saved location
                val location = LatLng(latitude, longitude)
                map.clear()  // Clear any previous markers
                map.addMarker(MarkerOptions().position(location))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        }
    }

    // Fetch place details using Place ID
    private fun fetchPlaceDetails(placeId: String) {
        val placesClient = Places.createClient(this)

        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val latLng = place.latLng
                if (latLng != null) {
                    latitude = latLng.latitude
                    longitude = latLng.longitude
                    // Move the camera to the selected place and add a marker
                    map.clear()  // Clear any previous markers
                    map.addMarker(MarkerOptions().position(latLng).title(place.name))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Maps", "Place not found: ${exception.message}")
            }
    }

}