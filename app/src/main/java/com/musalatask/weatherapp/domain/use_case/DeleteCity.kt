package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import com.musalatask.weatherapp.domain.repository.GeocodingRepository

/**
 * Use case that has the business logic related when a user wants to delete an
 * info of a city weather requested before by him. After doing this, it shouldn't
 * remains any entity stored in the app with [cityName] attribute.
 */
class DeleteCity(
    private val cityWeatherRepository: CityWeatherRepository,
    private val geocodingRepository: GeocodingRepository
) {

    suspend operator fun invoke(cityName: String){
        cityWeatherRepository.deleteCityWeather(cityName)
        geocodingRepository.deleteCoordinates(cityName)
    }
}