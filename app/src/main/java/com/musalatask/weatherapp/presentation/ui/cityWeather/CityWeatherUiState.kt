package com.musalatask.weatherapp.presentation.ui.cityWeather

import com.musalatask.weatherapp.domain.model.CityWeather

/**
 * Class which hold the ui state for city weather screen.
 */
data class CityWeatherUiState(
    val cityWeather: CityWeather? = null,
    val isLoading: Boolean = false,
    val errorResource: Int? = null
)
