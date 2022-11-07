package com.musalatask.weatherapp.domain.repository

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.CityWeather
import kotlinx.coroutines.flow.Flow

interface CityWeatherRepository {

    /**
     * Get a flow that emits resources of city weather objects with info about the state of
     * the request (can be Loading, Success and Error)
     *
     * @param[cityName] the name of the desired city.
     *
     * @return a flow of resource of the city weather object with the name provided.
     */
    fun getCityWeather(cityName: String): Flow<Resource<CityWeather?>>

    /**
     * Get a flow that emits resources of city weather objects with info about the state of
     * the request (can be Loading, Success and Error)
     *
     * @param[latitude] specific geographic latitude of the desired city.
     * @param[longitude] specific geographic longitude of the desired city.
     *
     * @return a flow of resource of the city weather object with the coordinates provided.
     */
    fun getCityWeather(latitude: Double, longitude: Double): Flow<Resource<CityWeather?>>

    fun getAllCityWeathers() : Flow<List<CityWeather>>

    /**
     * Delete the city weather object which contains [cityName] as attribute.
     *
     * @param[cityName] the name of the city.
     */
    suspend fun deleteCityWeather(cityName: String)

    /**
     * This function gets all city weather objects which its name contains [text]
     * as subsequence.
     *
     * @param[text] the subsequence for which you want to search.
     *
     * @return Flow of list of city weather found objects.
    **/
    fun getCityWeathersByText(text: String): Flow<List<CityWeather>>
}