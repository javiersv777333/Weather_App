package com.musalatask.weatherapp.data.local

import com.musalatask.weatherapp.domain.model.CityWeather
import kotlinx.coroutines.flow.Flow

/**
 * Data source that handle city weather objects locally .
 */
interface CityWeatherLocalDataSource {

    /**
     * Get a city weather object stored locally with a specific name.
     *
     * @param[cityName] the name for the city.
     *
     * @return a city weather object with [cityName] as a name,
     * null in case that there isn't one with that name.
     */
    suspend fun getCityWeather(cityName: String): CityWeather?

    suspend fun insertCityWeather(cityWeather: CityWeather)

    fun getAllCityWeathers(): Flow<List<CityWeather>>

    suspend fun deleteCityWeather(cityName: String)

    /**
     * This function gets all city weather objects which its name contains [text]
     * as subsequence.
     *
     * @param[text] the subsequence for which you want to search.
     *
     * @return Flow of list of city weather found objects.
     */
    fun getCityWeathersByText(text: String): Flow<List<CityWeather>>

    /**
     * Function used to execute a block of code with a certain asynchronous treatment,
     * related to the specific local nature of the data source.
     *
     * @param[block] block of code that you want to execute.
     */
    suspend fun <R> withAsynchronousContext(block: suspend () -> R): R
}