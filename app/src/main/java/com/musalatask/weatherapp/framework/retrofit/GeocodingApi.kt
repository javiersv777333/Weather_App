package com.musalatask.weatherapp.framework.retrofit

import com.musalatask.weatherapp.framework.retrofit.dto.CoordinatesDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * An endpoints interface for Retrofit.
 */
interface GeocodingApi {

    @GET("/geo/1.0/direct?limit=1")
    suspend fun getCoordinates(
        @Query("q") cityName: String
    ): List<CoordinatesDto>

    @GET("/geo/1.0/reverse?limit=1")
    suspend fun getCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): List<CoordinatesDto>
}