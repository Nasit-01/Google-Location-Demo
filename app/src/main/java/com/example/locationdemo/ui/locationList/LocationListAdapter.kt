package com.example.locationdemo.ui.locationList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.locationdemo.data.local.Location
import com.example.locationdemo.databinding.RowLocationItemBinding
import com.example.locationdemo.listener.OnRecyclerItemClickListener

class LocationListAdapter(
    var languageList: ArrayList<Location>,
    var itemClickListener: OnRecyclerItemClickListener
) : RecyclerView.Adapter<LocationListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RowLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowLocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(languageList[position]) {
                binding.tvCityName.text = this.name.substringBefore(",")
                binding.tvAddress.text = this.name
            }
            binding.ivEdit.setOnClickListener {
                itemClickListener.onRecyclerItemClicked(binding.ivEdit, position)
            }
            binding.ivDelete.setOnClickListener {
                itemClickListener.onRecyclerItemClicked(binding.ivDelete, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return languageList.size
    }
}

