package com.musalatask.weatherapp.data.local

import com.musalatask.weatherapp.data.model.Coordinates
import kotlinx.coroutines.flow.Flow

interface CoordinatesLocalDataSource {

    fun getCoordinatesOfACity(cityName: String): Flow<Coordinates?>
    suspend fun insertCoordinates(vararg coordinates: Coordinates)

    suspend fun <R> withTransaction(block: suspend () -> R): R
}