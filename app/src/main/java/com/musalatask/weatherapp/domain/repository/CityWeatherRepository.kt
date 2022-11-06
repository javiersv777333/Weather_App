package com.musalatask.weatherapp.domain.repository

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.CityWeather
import kotlinx.coroutines.flow.Flow

interface CityWeatherRepository {

    fun getCityWeather(cityName: String): Flow<Resource<CityWeather?>>
    fun getCityWeather(latitude: Double, longitude: Double): Flow<Resource<CityWeather?>>
    fun getAllCityWeathers() : Flow<List<CityWeather>>
    suspend fun deleteCityWeather(cityName: String)
}