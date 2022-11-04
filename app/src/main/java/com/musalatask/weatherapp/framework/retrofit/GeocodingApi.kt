package com.musalatask.weatherapp.framework.retrofit

import com.musalatask.weatherapp.framework.retrofit.dto.CoordinatesDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    @GET("/geo/1.0/direct?limit=1")
    suspend fun getCoordinates(
        @Query("q") cityName: String
    ): List<CoordinatesDto>
}