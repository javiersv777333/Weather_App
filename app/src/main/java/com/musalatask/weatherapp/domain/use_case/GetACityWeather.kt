package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetACityWeather(private val cityWeatherRepository: CityWeatherRepository) {

    operator fun invoke(cityName: String): Flow<Resource<CityWeather?>> =
        if (cityName.isBlank()) flow { }
        else cityWeatherRepository.getCityWeather(cityName)
}