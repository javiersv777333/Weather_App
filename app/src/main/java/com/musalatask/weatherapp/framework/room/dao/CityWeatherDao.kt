package com.musalatask.weatherapp.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.musalatask.weatherapp.framework.room.entity.CityWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityWeatherDao {
    @Query("SELECT * FROM city_weathers")
    fun getAll(): Flow<CityWeatherEntity>

    @Query("SELECT * FROM city_weathers WHERE cityName LIKE :cityName LIMIT 1")
    fun findByName(cityName: String): Flow<CityWeatherEntity>

    @Insert
    suspend fun insertAll(vararg cityWeatherEntities: CityWeatherEntity)

    @Delete
    suspend fun delete(cityWeatherEntity: CityWeatherEntity)

    @Query("DELETE FROM city_weathers")
    suspend fun clearCityWeatherEntities()
}