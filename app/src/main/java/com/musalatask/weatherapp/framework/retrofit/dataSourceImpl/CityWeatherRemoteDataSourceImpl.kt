package com.musalatask.weatherapp.framework.retrofit.dataSourceImpl

import com.musalatask.weatherapp.domain.model.CityWeather
import com.musalatask.weatherapp.data.remote.CityWeatherRemoteDataSource
import com.musalatask.weatherapp.framework.retrofit.CityWeatherApi
import com.musalatask.weatherapp.framework.retrofit.dto.toCityWeather
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A implementation for CityWeatherRemoteDataSource.
 *
 * @param[api] an interface which contains the endpoints.
 * @param[dispatcher] a coroutine dispatcher in which the requests
 * will be executed.
 */
class CityWeatherRemoteDataSourceImpl @Inject constructor(
    private val api: CityWeatherApi,
    private val dispatcher: CoroutineDispatcher
) : CityWeatherRemoteDataSource {

    /**
     * Get a city weather object from the backend with a specific coordinates.
     *
     * @param[latitude] the geographic latitude for the city.
     * @param[longitude] the geographic longitude for the city.
     *
     * @return a city weather object with [latitude] and [longitude] as its coordinates.
     */
    override suspend fun getCityWeather(latitude: Double, longitude: Double): CityWeather {
        return withContext(dispatcher) {
            api.getCityWeather(
                latitude = latitude,
                longitude = longitude
            ).toCityWeather()
        }
    }
}