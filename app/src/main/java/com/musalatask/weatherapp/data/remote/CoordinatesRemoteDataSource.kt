package com.musalatask.weatherapp.data.remote

import com.musalatask.weatherapp.data.model.Coordinates

interface CoordinatesRemoteDataSource {

    suspend fun getCoordinatesOfACity(cityName: String): Coordinates?
    suspend fun getCoordinates(latitude: Double, longitude: Double): Coordinates
}