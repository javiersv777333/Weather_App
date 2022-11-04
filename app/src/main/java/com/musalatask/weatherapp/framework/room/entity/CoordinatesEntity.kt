package com.musalatask.weatherapp.framework.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.framework.retrofit.dto.CoordinatesDto

@Entity(tableName = "coordinates")
data class CoordinatesEntity(
    @PrimaryKey val cityName: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)

fun CoordinatesEntity.toCoordinates(): Coordinates =
    Coordinates(
        latitude = latitude,
        longitude = longitude,
        cityName = cityName
    )
