package com.musalatask.weatherapp.framework.room.dataSourceImpl

import androidx.room.withTransaction
import com.musalatask.weatherapp.data.local.CityWeatherLocalDataSource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.model.toCityWeatherEntity
import com.musalatask.weatherapp.framework.room.db.AppDataBase
import com.musalatask.weatherapp.framework.room.entity.toCityWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A Room implementation for CityWeatherLocalDataSource.
 *
 * @param[db] a Room database.
 */
class CityWeatherLocalDataSourceImpl @Inject constructor(
    private val db: AppDataBase
) : CityWeatherLocalDataSource {

    /**
     * Get a city weather object stored in [db] with a specific name.
     *
     * @param[cityName] the name for the city.
     *
     * @return a city weather object with [cityName] as a name,
     * null in case that there isn't one with that name.
     */
    override suspend fun getCityWeather(cityName: String): CityWeather? {
        return db.cityWeatherDao().findByName(cityName)?.toCityWeather()
    }

    override suspend fun insertCityWeather(cityWeather: CityWeather) {
        db.cityWeatherDao().insertAll(cityWeather.toCityWeatherEntity())
    }

    override fun getAllCityWeathers(): Flow<List<CityWeather>> =
        db.cityWeatherDao().getAll().map { it.map { it.toCityWeather() } }

    override suspend fun deleteCityWeather(cityName: String) {
        withAsynchronousContext {
            db.cityWeatherDao().delete(cityName)
        }
    }

    /**
     * This function gets all city weather objects which its name contains [text]
     * as subsequence.
     *
     * @param[text] the subsequence for which you want to search.
     *
     * @return Flow of list of city weather found objects.
     */
    override fun getCityWeathersByText(text: String): Flow<List<CityWeather>> {
        val lowerCaseText = text.lowercase()
        return getAllCityWeathers().map { list ->
            list.filter {
                it.cityName.lowercase().contains(lowerCaseText)
            }
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