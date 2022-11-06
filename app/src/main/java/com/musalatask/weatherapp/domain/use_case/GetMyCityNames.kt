package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMyCityNames(private val cityWeatherRepository: CityWeatherRepository) {

    operator fun invoke() : Flow<List<String>> =
        cityWeatherRepository.getAllCityWeathers().map { it.map { it.cityName } }
}