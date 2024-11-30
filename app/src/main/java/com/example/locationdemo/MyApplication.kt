package com.example.locationdemo

import android.app.Application
import com.example.locationdemo.utils.Constant.PLACE_API_KEY
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, PLACE_API_KEY)
    }
}
