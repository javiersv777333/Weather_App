package com.musalatask.weatherapp.domain.repository

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.Coordinates
import kotlinx.coroutines.flow.Flow

interface GeocodingRepository {

    fun getCoordinatesOfACity(cityName: String): Flow<Resource<Coordinates?>>
    fun getCoordinatesOfACity(latitude: Double, longitude: Double): Flow<Resource<Coordinates?>>
}