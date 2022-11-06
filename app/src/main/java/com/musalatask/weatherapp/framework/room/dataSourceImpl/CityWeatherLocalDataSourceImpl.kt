package com.musalatask.weatherapp.framework.room.dataSourceImpl

import android.util.Log
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

    override suspend fun getCityWeather(cityName: String): CityWeather? {
        Log.d("qqqqqqqq", "get $cityName weather from db")
        return db.cityWeatherDao().findByName(cityName)?.toCityWeather()
    }

    override suspend fun insertCityWeather(cityWeather: CityWeather) {
        db.cityWeatherDao().insertAll(cityWeather.toCityWeatherEntity())
    }

    override fun getAllCityWeathers(): Flow<List<CityWeather>> =
        db.cityWeatherDao().getAll().map { it.map { it.toCityWeather() } }

    override suspend fun deleteCityWeather(cityName: String) {
        withTransaction {
            db.cityWeatherDao().delete(cityName)
        }
    }

    override fun getCityWeathersByText(text: String): Flow<List<CityWeather>> {
        val lowerCaseText = text.lowercase()
        return getAllCityWeathers().map {
            it.filter {
                it.cityName.lowercase()
                    .contains(lowerCaseText) || it.coordinatesName != null && it.coordinatesName!!.lowercase()
                    .contains(
                        lowerCaseText
                    )
            }
        }
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R =
        db.withTransaction(block)

}