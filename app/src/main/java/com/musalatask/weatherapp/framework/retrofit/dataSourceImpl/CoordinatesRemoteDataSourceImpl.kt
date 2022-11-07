package com.musalatask.weatherapp.framework.retrofit.dataSourceImpl

import android.util.Log
import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.data.remote.CoordinatesRemoteDataSource
import com.musalatask.weatherapp.framework.retrofit.GeocodingApi
import com.musalatask.weatherapp.framework.retrofit.dto.toCoordinates
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A implementation for CoordinatesRemoteDataSource.
 *
 * @param[api] an interface which contains the endpoints.
 * @param[dispatcher] a coroutine dispatcher in which the requests
 * will be executed.
 */
class CoordinatesRemoteDataSourceImpl @Inject constructor(
    private val api: GeocodingApi,
    private val dispatcher: CoroutineDispatcher
) : CoordinatesRemoteDataSource {

    /**
     * Get a city coordinates object stored locally with a specific name.
     *
     * @param[cityName] the name for the city.
     *
     * @return a city coordinates object with [cityName] as a name,
     * null in case that there isn't one with that name.
     */
    override suspend fun getCoordinatesOfACity(cityName: String): Coordinates? {
        return withContext(dispatcher) {
            val result = api.getCoordinates(cityName)
            if (result.isNotEmpty()) result[0].toCoordinates() else null
        }
    }

    /**
     * Get a city coordinates object from the backend with a specific coordinates.
     *
     * @param[latitude] the geographic latitude for the city.
     * @param[longitude] the geographic longitude for the city.
     *
     * @return a city coordinate object with [latitude] and [longitude] as its coordinates.
     * null in case in which there is no one.
     */
    override suspend fun getCoordinates(latitude: Double, longitude: Double): Coordinates? {
        return withContext(dispatcher) {
            api.getCoordinates(latitude = latitude, longitude = longitude)[0].toCoordinates()
        }
    }
}