package com.musalatask.weatherapp.framework.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.musalatask.weatherapp.framework.room.dao.CityWeatherDao
import com.musalatask.weatherapp.framework.room.dao.CoordinatesDao
import com.musalatask.weatherapp.framework.room.entity.CityWeatherEntity
import com.musalatask.weatherapp.framework.room.entity.CoordinatesEntity

@Database(entities = [CityWeatherEntity::class, CoordinatesEntity::class], version = 1)
@TypeConverters(com.musalatask.weatherapp.framework.room.TypeConverters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun cityWeatherDao(): CityWeatherDao
    abstract fun coordinatesDao(): CoordinatesDao

    suspend fun clear() {
        cityWeatherDao().clearCityWeatherEntities()
        coordinatesDao().clearCoordinatesEntities()
    }
}