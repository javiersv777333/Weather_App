package com.musalatask.weatherapp.framework.retrofit.dataSourceImpl

import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.data.remote.CoordinatesRemoteDataSource
import com.musalatask.weatherapp.framework.retrofit.GeocodingApi
import com.musalatask.weatherapp.framework.retrofit.dto.toCoordinates
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoordinatesRemoteDataSourceImpl @Inject constructor(
    private val api: GeocodingApi,
    private val dispatcher: CoroutineDispatcher
) : CoordinatesRemoteDataSource {

    override suspend fun getCoordinatesOfACity(cityName: String): Coordinates? =
        withContext(dispatcher) {
            val result = api.getCoordinates(cityName)
            if (result.isNotEmpty()) result[0].toCoordinates() else null
        }

    override suspend fun getCoordinates(latitude: Double, longitude: Double): Coordinates =
        withContext(dispatcher) {
            api.getCoordinates(latitude = latitude, longitude = longitude)[0].toCoordinates()
        }
}