package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow

class GetMyCurrentCityWeather(private val cityWeatherRepository: CityWeatherRepository) {

    operator fun invoke(
        latitude: Double,
        longitude: Double
    ): Flow<Resource<CityWeather?>> =
        cityWeatherRepository.getCityWeather(latitude = latitude, longitude = longitude)
}