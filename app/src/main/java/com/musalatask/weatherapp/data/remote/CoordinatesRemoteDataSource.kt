package com.musalatask.weatherapp.data.remote

import com.musalatask.weatherapp.data.model.Coordinates

interface CoordinatesRemoteDataSource {

    suspend fun getCoordinatesOfACity(cityName: String): Coordinates?
}