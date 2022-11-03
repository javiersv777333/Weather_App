package com.musalatask.weatherapp.framework.retrofit

import com.musalatask.weatherapp.framework.retrofit.dto.CoordinatesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    @GET("/direct?limit=1")
    suspend fun getCoordinates(@Query("appid") apiKey: String, @Query("q") cityName: String): List<CoordinatesDto>
}