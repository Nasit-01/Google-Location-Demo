package com.example.locationdemo.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update


@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: Location)

    @Query("UPDATE locations SET name = :name, latitude = :latitude, longitude = :longitude WHERE id = :id")
    suspend fun updateLocationById(id: Long, name: String, latitude: Double, longitude: Double)

    @Delete
    suspend fun delete(location: Location)

    @Query("SELECT * FROM locations ORDER BY name ASC")
    suspend fun getAllLocations(): List<Location>
}

@Database(entities = [Location::class], version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}