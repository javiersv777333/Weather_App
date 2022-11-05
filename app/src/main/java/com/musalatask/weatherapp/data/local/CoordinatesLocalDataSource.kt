package com.musalatask.weatherapp.data.local

import com.musalatask.weatherapp.data.model.Coordinates
import kotlinx.coroutines.flow.Flow

interface CoordinatesLocalDataSource {

    suspend fun getCoordinatesOfACity(cityName: String): Coordinates?
    suspend fun insertCoordinates(vararg coordinates: Coordinates)

    suspend fun <R> withTransaction(block: suspend () -> R): R
}