package com.musalatask.weatherapp.framework.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.musalatask.weatherapp.domain.model.Coordinates

@Entity(tableName = "coordinates")
data class CoordinatesEntity(
    @PrimaryKey val cityName: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "names_for_search") val names: List<String>//This store the different names
    // that the user used to search for this coordinates.
)

/**
 * Map a room city coordinates entity object into a city coordinates model object.
 */
fun CoordinatesEntity.toCoordinates(): Coordinates =
    Coordinates(
        latitude = latitude,
        longitude = longitude,
        cityName = cityName,
        names = ArrayList(names)
    )
