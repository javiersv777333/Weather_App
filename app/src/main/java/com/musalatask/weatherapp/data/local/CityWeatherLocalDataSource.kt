package com.musalatask.weatherapp.data.local

import com.musalatask.weatherapp.data.model.CityWeather
import kotlinx.coroutines.flow.Flow

interface CityWeatherLocalDataSource {

    suspend fun getCityWeather(cityName: String): CityWeather?
    suspend fun insertCityWeather(cityWeather: CityWeather)
    suspend fun <R> withTransaction(block: suspend () -> R): R
}