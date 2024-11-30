package com.example.locationdemo.listener

import android.view.View

interface OnRecyclerItemClickListener {
    fun onRecyclerItemClicked(view: View?, position: Int)
}