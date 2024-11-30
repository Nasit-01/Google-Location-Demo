package com.example.locationdemo.ui.locationList

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationdemo.R
import com.example.locationdemo.data.local.Location
import com.example.locationdemo.databinding.ActivityLocationListBinding
import com.example.locationdemo.listener.OnRecyclerItemClickListener
import com.example.locationdemo.ui.addLocation.AddLocationActivity
import com.example.locationdemo.ui.mapPreview.MapPreviewActivity
import com.example.locationdemo.utils.Params.INTENT_IS_FROM_UPDATE
import com.example.locationdemo.utils.Params.INTENT_LOCATION
import com.example.locationdemo.utils.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationListActivity : AppCompatActivity() {
    lateinit var binding: ActivityLocationListBinding
    private lateinit var viewModel: LocationListViewModel
    private lateinit var locationListAdapter: LocationListAdapter
    private var locationList: ArrayList<Location> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this).get(LocationListViewModel::class.java)
        initView()
        subscribeObserver()
    }

    fun initView() {
        Util.initToolBarTitle(getString(R.string.location_list), this)
        binding.toolbar.imgBackAerrow.visibility = View.GONE

        binding.rvLocationList.setLayoutManager(LinearLayoutManager(this))

        locationListAdapter =
            LocationListAdapter(locationList, object : OnRecyclerItemClickListener {
                override fun onRecyclerItemClicked(view: View?, position: Int) {

                    when (view?.id) {
                        R.id.ivDelete -> {
                            viewModel.deleteLocation(locationList.get(position))
                            locationList.removeAt(position)
                            locationListAdapter.notifyItemRemoved(position)
                            if (locationList.isEmpty()){
                                binding.btnViewMap.visibility = View.GONE
                                binding.rtlSortView.visibility = View.GONE
                                binding.rvLocationList.visibility = View.GONE
                                binding.tvNoLocationMsg.visibility = View.VISIBLE
                            }
                        }

                        R.id.ivEdit -> {
                            val intent =
                                Intent(this@LocationListActivity, AddLocationActivity::class.java)
                            intent.putExtra(INTENT_LOCATION, locationList.get(position))
                            intent.putExtra(INTENT_IS_FROM_UPDATE, true)
                            startActivity(intent)
                        }
                    }

                }
            })
        binding.rvLocationList.adapter = locationListAdapter

        binding.btnAddLocation.setOnClickListener {
            val intent = Intent(this, AddLocationActivity::class.java)
            startActivity(intent)
        }

        binding.btnViewMap.setOnClickListener {
            val intent = Intent(this, MapPreviewActivity::class.java)
            startActivity(intent)
        }

        binding.radioGroupSortOrder.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonAsc -> viewModel.setSortOrder("ASC")
                R.id.radioButtonDesc -> viewModel.setSortOrder("DESC")
            }
            // After changing the sorting order, reload the locations
            viewModel.loadLocations()
        }
    }

    fun subscribeObserver() {
        viewModel.getAllLocationResponse.observe(this) { locations ->
            locationList.clear()
            locationList.addAll(locations)
            locationListAdapter.notifyDataSetChanged()

            if (locationList.isNotEmpty()) {
                binding.btnViewMap.visibility = View.VISIBLE
                binding.rtlSortView.visibility = View.VISIBLE
                binding.rvLocationList.visibility = View.VISIBLE
                binding.tvNoLocationMsg.visibility = View.GONE
            } else {
                binding.btnViewMap.visibility = View.GONE
                binding.rtlSortView.visibility = View.GONE
                binding.rvLocationList.visibility = View.GONE
                binding.tvNoLocationMsg.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllLocation()
    }
}