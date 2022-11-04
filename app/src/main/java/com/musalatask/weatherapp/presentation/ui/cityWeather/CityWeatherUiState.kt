package com.musalatask.weatherapp.presentation.ui.cityWeather

import com.musalatask.weatherapp.data.model.CityWeather

data class CityWeatherUiState(
    val cityWeather: CityWeather? = null,
    val isLoathing: Boolean = false,
)
