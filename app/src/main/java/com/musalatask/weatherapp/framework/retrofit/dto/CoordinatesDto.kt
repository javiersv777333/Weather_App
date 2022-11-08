package com.musalatask.weatherapp.framework.retrofit.dto

import com.musalatask.weatherapp.domain.model.Coordinates

data class CoordinatesDto(
    val country: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String
)

/**
 * Map a coordinates dto object into a model coordinates object.
 */
fun CoordinatesDto.toCoordinates(): Coordinates =
    Coordinates(
        latitude = lat,
        longitude = lon,
        cityName = name,
        names = mutableListOf(name)
    )