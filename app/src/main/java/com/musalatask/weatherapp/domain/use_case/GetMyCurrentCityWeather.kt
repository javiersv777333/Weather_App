package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.domain.model.CityWeather
import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case that has the business logic related with requesting the weather by its geographical
 * coordinates ([latitue] and [longitude])and is executed when a user launch the app
 *
 */
class GetMyCurrentCityWeather(private val cityWeatherRepository: CityWeatherRepository) {

    operator fun invoke(
        latitude: Double,
        longitude: Double
    ): Flow<Resource<CityWeather?>> =
        cityWeatherRepository.getCityWeather(latitude = latitude, longitude = longitude)
}