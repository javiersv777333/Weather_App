package com.musalatask.weatherapp.framework.retrofit

import com.musalatask.weatherapp.framework.retrofit.dto.CityWeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An endpoints interface for Retrofit.
 */
interface CityWeatherApi {

    @GET("/data/2.5/weather")
    suspend fun getCityWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): CityWeatherDto
}