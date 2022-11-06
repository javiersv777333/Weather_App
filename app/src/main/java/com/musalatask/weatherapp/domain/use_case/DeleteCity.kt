package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import com.musalatask.weatherapp.domain.repository.GeocodingRepository

class DeleteCity(
    private val cityWeatherRepository: CityWeatherRepository,
    private val geocodingRepository: GeocodingRepository
) {

    suspend operator fun invoke(cityName: String){
        cityWeatherRepository.deleteCityWeather(cityName)
        geocodingRepository.deleteCoordinates(cityName)
    }
}