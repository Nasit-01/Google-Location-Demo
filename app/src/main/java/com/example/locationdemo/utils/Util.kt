package com.example.locationdemo.utils

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.locationdemo.R

object Util {

    fun initToolBarTitle(title: String,activity: AppCompatActivity) {
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            val tvTitle = toolbar.findViewById<TextView>(R.id.tvTitle)
            tvTitle.text = title
            tvTitle.visibility = View.VISIBLE
        } else {
            Log.e(TAG, "Toolbar not found in layout")
        }
    }


    fun getDistanceTo(locLat:Double,locLang:Double,lat: Double, lon: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(locLat, locLat, lat, lon, results)
        return results[0]
    }

    fun hideKeyboard(activity: Activity) {
        try {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (e: Exception) {
            Log.e(TAG, "Can't hide keyboard " + e.message)
        }
    }
}