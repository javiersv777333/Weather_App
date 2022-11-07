package com.musalatask.weatherapp.domain.repository

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.Coordinates
import kotlinx.coroutines.flow.Flow

interface GeocodingRepository {

    /**
     * Get a flow that emits resources of city coordinates objects with info about the state of
     * the request (can be Loading, Success and Error)
     *
     * @param[cityName] the name of the desired city.
     *
     * @return a flow of resource of the city coordinates object with the name provided.
     */
    fun getCoordinatesOfACity(cityName: String): Flow<Resource<Coordinates?>>

    /**
     * Get a flow that emits resources of city coordinates objects with info about the state of
     * the request (can be Loading, Success and Error)
     *
     * @param[latitude] the geographic latitude for the city.
     * @param[longitude] the geographic longitude for the city.
     *
     * @returnGet a flow of resource of the city coordinates object with the geographical coordinates provided.
     */
    fun getCoordinatesOfACity(latitude: Double, longitude: Double): Flow<Resource<Coordinates?>>

    /**
     * Delete the city coordinates object which contains [cityName] as attribute.
     *
     * @param[cityName] the name of the city.
     */
    suspend fun deleteCoordinates(cityName: String)
}