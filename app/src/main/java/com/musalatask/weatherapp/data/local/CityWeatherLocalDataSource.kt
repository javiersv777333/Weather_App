package com.musalatask.weatherapp.data.local

import com.musalatask.weatherapp.data.model.CityWeather
import kotlinx.coroutines.flow.Flow

interface CityWeatherLocalDataSource {

    fun getCityWeather(cityName: String): Flow<CityWeather>
}