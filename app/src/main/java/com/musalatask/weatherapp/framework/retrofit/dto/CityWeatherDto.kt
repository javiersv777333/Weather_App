package com.musalatask.weatherapp.framework.retrofit.dto

import com.musalatask.weatherapp.domain.model.CityWeather

data class CityWeatherDto(
    val base: String?,
    val clouds: Clouds?,
    val cod: Int?,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int?,
    val weather: List<Weather>,
    val wind: Wind,
    val rain: Rain?,
    val snow: Snow?
)

/**
 * Map a city weather dto object into a model city weather object.
 */
fun CityWeatherDto.toCityWeather(): CityWeather =
    CityWeather(
        description = if (weather.isNotEmpty()) weather[0].description else null,
        iconId = if (weather.isNotEmpty()) weather[0].icon else null,
        temp = main.temp,
        feelsLike = main.feels_like,
        tempMin = main.temp_min,
        tempMax = main.temp_max,
        pressure = main.pressure,
        humidity = main.humidity,
        visibility = visibility,
        windSpeed = wind.speed,
        windDirection = wind.deg,
        cloudiness = clouds?.all,
        rainVolumeLastHour = rain?.lastHour,
        snowVolumeLastHour = snow?.lastHour,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        timezone = timezone,
        cityName = name,
        latitude = coord.lat,
        longitude = coord.lon,
        lastUpdated = null
    )