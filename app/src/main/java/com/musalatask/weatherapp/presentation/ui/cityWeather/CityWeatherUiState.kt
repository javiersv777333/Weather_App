package com.musalatask.weatherapp.presentation.ui.cityWeather

import com.musalatask.weatherapp.data.model.CityWeather

data class CityWeatherUiState(
    val cityWeather: CityWeather? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
