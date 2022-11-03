package com.musalatask.weatherapp.framework.room.dataSourceImpl

import com.musalatask.weatherapp.data.local.CityWeatherLocalDataSource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.framework.room.db.AppDataBase
import com.musalatask.weatherapp.framework.room.entity.toCityWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityWeatherLocalDataSourceImpl @Inject constructor(
    private val db: AppDataBase
) : CityWeatherLocalDataSource {

    override fun getCityWeather(cityName: String): Flow<CityWeather> =
        db.cityWeatherDao().findByName(cityName).map { it.toCityWeather() }

}