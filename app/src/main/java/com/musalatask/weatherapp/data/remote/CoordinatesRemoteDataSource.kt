package com.musalatask.weatherapp.data.remote

import com.musalatask.weatherapp.data.model.Coordinates

/**
 * Data source that handle city coordinates objects remotely .
 */
interface CoordinatesRemoteDataSource {

    /**
     * Get a city coordinates object from the backend with a specific name.
     *
     * @param[cityName] the name for the city.
     *
     * @return a city coordinate object with [cityName] as its name.
     * null in case in which there is no one.
     */
    suspend fun getCoordinatesOfACity(cityName: String): Coordinates?

    /**
     * Get a city coordinates object from the backend with a specific coordinates.
     *
     * @param[latitude] the geographic latitude for the city.
     * @param[longitude] the geographic longitude for the city.
     *
     * @return a city coordinate object with [latitude] and [longitude] as its coordinates.
     * null in case in which there is no one.
     */
    suspend fun getCoordinates(latitude: Double, longitude: Double): Coordinates?
}