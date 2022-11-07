package com.musalatask.weatherapp.framework.room.dataSourceImpl

import android.util.Log
import androidx.room.withTransaction
import com.musalatask.weatherapp.data.local.CoordinatesLocalDataSource
import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.data.model.toCoordinatesEntity
import com.musalatask.weatherapp.framework.room.db.AppDataBase
import com.musalatask.weatherapp.framework.room.entity.toCoordinates
import javax.inject.Inject

/**
 * A Room implementation for CityWeatherLocalDataSource.
 *
 * @param[db] a Room database.
 */
class CoordinatesLocalDataSourceImpl @Inject constructor(
    private val db: AppDataBase
) : CoordinatesLocalDataSource {

    /**
     * Get a city coordinates object stored in [db] with a specific name.
     *
     * @param[cityName] the name for the city.
     *
     * @return a city coordinates object with [cityName] as a name or with
     * names field which contains [cityName], null in case that there isn't one with that name.
     */
    override suspend fun getCoordinatesOfACity(cityName: String): Coordinates? {
        return db.coordinatesDao().getAll().filter {
            cityName.lowercase() == it.cityName.lowercase() ||
            it.names.map { it.lowercase() }.contains(cityName.lowercase())}
            .take(1).getOrNull(0)?.toCoordinates()
    }

    override suspend fun insertCoordinates(vararg coordinates: Coordinates) {
        db.coordinatesDao().insertAll(coordinates.map { it.toCoordinatesEntity() })
    }

    override suspend fun deleteCoordinates(cityName: String) {
        withAsynchronousContext {
            db.coordinatesDao().delete(cityName)
        }
    }

    /**
     * Function used to execute a block of code with a certain asynchronous treatment.
     * For this implementation withTransaction(block) function provided by Room was selected
     * as a asynchronous treatment.
     *
     * @param[block] block of code that you want to execute.
     */
    override suspend fun <R> withAsynchronousContext(block: suspend () -> R): R =
        db.withTransaction(block)
}