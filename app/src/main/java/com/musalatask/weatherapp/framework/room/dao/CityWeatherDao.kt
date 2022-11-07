package com.musalatask.weatherapp.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.musalatask.weatherapp.framework.room.entity.CityWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityWeatherDao {
    @Query("SELECT * FROM city_weathers ORDER BY cityName")
    fun getAll(): Flow<List<CityWeatherEntity>>

    @Query("SELECT * FROM city_weathers WHERE cityName LIKE :cityName LIMIT 1")
    suspend fun findByName(cityName: String): CityWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg cityWeatherEntities: CityWeatherEntity)

    @Query("DELETE FROM city_weathers WHERE cityName LIKE :cityName")
    suspend fun delete(cityName: String)

    @Delete
    suspend fun delete(cityWeatherEntity: CityWeatherEntity)

    @Query("DELETE FROM city_weathers")
    suspend fun clearCityWeatherEntities()
}