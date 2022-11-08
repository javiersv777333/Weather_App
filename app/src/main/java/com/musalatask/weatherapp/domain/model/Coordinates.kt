package com.musalatask.weatherapp.domain.model

import com.musalatask.weatherapp.framework.room.entity.CoordinatesEntity

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val names: MutableList<String>//This store the different names that the user used
    //to search for this coordinates.
)

/**
 * Map a coordinates model object into a room coordinates entity object.
 */
fun Coordinates.toCoordinatesEntity(): CoordinatesEntity =
    CoordinatesEntity(
        latitude = latitude,
        longitude = longitude,
        cityName = cityName,
        names = names
    )