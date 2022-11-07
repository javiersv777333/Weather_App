package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case that has the business logic related when a user wants to gets all the
 * cities that he already requested its weathers.
 */
class GetMyCityNames(private val cityWeatherRepository: CityWeatherRepository) {

    operator fun invoke() : Flow<List<String>> =
        cityWeatherRepository.getAllCityWeathers().map { it.map { it.cityName } }
}