package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case that has the business logic related when a user wants to filter the list
 * of cities that he already retrieved by some text ([text]) which him entered in
 * the search view.
 */
class GetCitiesByText(private val cityWeatherRepository: CityWeatherRepository) {

    operator fun invoke(text: String): Flow<List<String>> =
        cityWeatherRepository.getCityWeathersByText(text).map { it.map { it.cityName } }
}