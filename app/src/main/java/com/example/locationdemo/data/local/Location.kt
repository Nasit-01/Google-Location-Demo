package com.example.locationdemo.data.local

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable {

    // Implement describeContents() method
    override fun describeContents(): Int {
        return 0
    }

    // Implement writeToParcel() method
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    // Companion object to create the Parcelable object from a Parcel
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Location> = object : Parcelable.Creator<Location> {
            override fun createFromParcel(parcel: Parcel): Location {
                return Location(
                    id = parcel.readLong(),
                    name = parcel.readString() ?: "",
                    latitude = parcel.readDouble(),
                    longitude = parcel.readDouble()
                )
            }

            override fun newArray(size: Int): Array<Location?> {
                return arrayOfNulls(size)
            }
        }
    }
}
