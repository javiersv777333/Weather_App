package com.musalatask.weatherapp.data.remote

import com.musalatask.weatherapp.domain.model.CityWeather

/**
 * Data source that handle city weather objects remotely .
 */
interface CityWeatherRemoteDataSource {

    /**
     * Get a city weather object from the backend with a specific coordinates.
     *
     * @param[latitude] the geographic latitude for the city.
     * @param[longitude] the geographic longitude for the city.
     *
     * @return a city weather object with [latitude] and [longitude] as its coordinates.
     */
    suspend fun getCityWeather(latitude: Double, longitude: Double): CityWeather
}