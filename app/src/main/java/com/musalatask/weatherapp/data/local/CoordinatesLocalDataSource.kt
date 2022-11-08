package com.musalatask.weatherapp.data.local

import com.musalatask.weatherapp.domain.model.Coordinates

/**
 * Data source that handle coordinates objects locally .
 */
interface CoordinatesLocalDataSource {

    /**
     * Get a city coordinates object stored locally with a specific name.
     *
     * @param[cityName] the name for the city.
     *
     * @return a city coordinates object with [cityName] as a name,
     * null in case that there isn't one with that name.
     */
    suspend fun getCoordinatesOfACity(cityName: String): Coordinates?

    suspend fun insertCoordinates(vararg coordinates: Coordinates)

    suspend fun deleteCoordinates(cityName: String)

    /**
     * Function used to execute a block of code with a certain asynchronous treatment,
     * related to the specific local nature of the data source.
     *
     * @param[block] block of code that you want to execute.
     */
    suspend fun <R> withAsynchronousContext(block: suspend () -> R): R
}