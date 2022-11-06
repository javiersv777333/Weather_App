package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCitiesByText(private val cityWeatherRepository: CityWeatherRepository) {

    operator fun invoke(text: String): Flow<List<String>> =
        cityWeatherRepository.getCityWeathersByText(text).map { it.map { it.cityName } }
}