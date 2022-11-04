package com.musalatask.weatherapp.data.remote

import com.musalatask.weatherapp.data.model.CityWeather

interface CityWeatherRemoteDataSource {

    suspend fun getCityWeather(latitude: Double, longitude: Double): CityWeather
}