package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.domain.model.CityWeather
import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case that has the business logic related when a user wants to get the weather
 * of a city entering its name ([cityName])
 */
class GetACityWeather(private val cityWeatherRepository: CityWeatherRepository) {

    operator fun invoke(cityName: String): Flow<Resource<CityWeather?>> =
        if (cityName.isBlank()) flow { }
        else cityWeatherRepository.getCityWeather(cityName)
}