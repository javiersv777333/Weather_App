package com.musalatask.weatherapp.framework.retrofit.dataSourceImpl

import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.remote.CityWeatherRemoteDataSource

class CityWeatherRemoteDataSourceImpl : CityWeatherRemoteDataSource {

    override suspend fun getCityWeather(cityName: String): CityWeather {
        TODO("Not yet implemented")
    }
}