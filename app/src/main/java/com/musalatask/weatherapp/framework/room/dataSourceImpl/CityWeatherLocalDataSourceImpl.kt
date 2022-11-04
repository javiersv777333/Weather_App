package com.musalatask.weatherapp.framework.room.dataSourceImpl

import androidx.room.withTransaction
import com.musalatask.weatherapp.data.local.CityWeatherLocalDataSource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.data.model.toCityWeatherEntity
import com.musalatask.weatherapp.data.model.toCoordinatesEntity
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

    override suspend fun insertCityWeather(cityWeather: CityWeather) {
        db.cityWeatherDao().insertAll(cityWeather.toCityWeatherEntity())
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R =
        db.withTransaction(block)

}