package com.musalatask.weatherapp.framework.room.dao

import androidx.room.*
import com.musalatask.weatherapp.framework.room.entity.CoordinatesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordinatesDao {
    @Query("SELECT * FROM coordinates")
    suspend fun getAll(): List<CoordinatesEntity>

    @Query("SELECT * FROM coordinates WHERE cityName LIKE :cityName LIMIT 1")
    suspend fun findByName(cityName: String): CoordinatesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coordinatesEntities: List<CoordinatesEntity>)

    @Query("DELETE FROM coordinates WHERE cityName LIKE :cityName")
    suspend fun delete(cityName: String)

    @Delete
    suspend fun delete(coordinatesEntity: CoordinatesEntity)

    @Query("DELETE FROM coordinates")
    suspend fun clearCoordinatesEntities()
}