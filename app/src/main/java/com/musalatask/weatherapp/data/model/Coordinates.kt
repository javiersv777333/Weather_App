package com.musalatask.weatherapp.data.model

import com.musalatask.weatherapp.framework.room.entity.CoordinatesEntity

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val cityName: String
)

fun Coordinates.toCoordinatesEntity(): CoordinatesEntity =
    CoordinatesEntity(
        latitude = latitude,
        longitude = longitude,
        cityName = cityName
    )