package com.musalatask.weatherapp.data.model

data class CityWeather(
    val description: String?,
    val iconId: String?,
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int?,
    val windSpeed: Double,
    val windDirection: Int,
    val cloudiness: Int?,
    val rainVolumeLastHour: Double?,
    val snowVolumeLastHour: Double?,
    val sunrise: Int,
    val sunset: Int,
    val timezone: Int,
    val cityName: String,
    val lastUpdated: Int?
)