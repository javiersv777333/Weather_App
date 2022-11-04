package com.musalatask.weatherapp.framework.retrofit.dto

import com.musalatask.weatherapp.data.model.Coordinates

data class CoordinatesDto(
    val country: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String
)

fun CoordinatesDto.toCoordinates(): Coordinates =
    Coordinates(
        latitude = lat,
        longitude = lon,
        cityName = name
    )