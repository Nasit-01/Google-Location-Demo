package com.example.locationdemo.ui.addLocation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.locationdemo.data.remote.PredictionsItem
import com.example.locationdemo.databinding.RowSearchedLocationItemBinding
import com.example.locationdemo.listener.OnRecyclerItemClickListener

class SearchedLocationAdapter(
    var locationList: ArrayList<PredictionsItem>,
    var itemClickListener: OnRecyclerItemClickListener
) : RecyclerView.Adapter<SearchedLocationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RowSearchedLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowSearchedLocationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(locationList[position]) {
                binding.tvlocation.text = this.description
            }
            binding.root.setOnClickListener {
                itemClickListener.onRecyclerItemClicked(binding.root, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }
}

