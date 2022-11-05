package com.musalatask.weatherapp.framework.retrofit.dataSourceImpl

import android.util.Log
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.remote.CityWeatherRemoteDataSource
import com.musalatask.weatherapp.framework.retrofit.CityWeatherApi
import com.musalatask.weatherapp.framework.retrofit.dto.toCityWeather
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CityWeatherRemoteDataSourceImpl @Inject constructor(
    private val api: CityWeatherApi,
    private val dispatcher: CoroutineDispatcher
) : CityWeatherRemoteDataSource {

    override suspend fun getCityWeather(latitude: Double, longitude: Double): CityWeather {
        Log.d("qqqqqqqq", "get latitud: $latitude longitude: $longitude weather from network")
        return withContext(dispatcher) {
            api.getCityWeather(
                latitude = latitude,
                longitude = longitude
            ).toCityWeather()
        }
    }
}