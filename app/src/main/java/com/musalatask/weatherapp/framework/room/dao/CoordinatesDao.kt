package com.musalatask.weatherapp.framework.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.musalatask.weatherapp.framework.room.entity.CoordinatesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordinatesDao {
    @Query("SELECT * FROM coordinates")
    fun getAll(): Flow<List<CoordinatesEntity>>

    @Query("SELECT * FROM coordinates WHERE cityName LIKE :cityName LIMIT 1")
    fun findByName(cityName: String): Flow<CoordinatesEntity?>

    @Insert
    suspend fun insertAll(vararg coordinatesEntities: List<CoordinatesEntity>)

    @Delete
    suspend fun delete(coordinatesEntity: CoordinatesEntity)

    @Query("DELETE FROM coordinates")
    suspend fun clearCoordinatesEntities()
}