package com.musalatask.weatherapp.framework.retrofit

import com.musalatask.weatherapp.framework.retrofit.dto.CityWeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CityWeatherApi {

    @GET("/weather")
    suspend fun getCityWeather(
        @Query("appid") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): CityWeatherDto
}