package com.musalatask.weatherapp.data.utils

import com.musalatask.weatherapp.R
import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.model.Coordinates
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

/**
 * This function create a workflow which request the cached data, verify if an update is needed,
 * request an update and save the result.
 * Trought this function the city weather objects are provided to the repository.
 *
 * @param[query] a query to obtain the cached data. This is the selected
 * SINGLE SOURCE OF TRUTH of the data.
 * @param[fetch] a query to retrieve the updates from the network.
 * @param[saveFetchResult] a lambda that save the network retrieved data.
 *
 * @return a Flow of city weather object resources that show the state of the data (data could be Loading, Success and Error)
 */
fun getNetworkBoundWeatherResource(
    query: suspend () -> CityWeather?,
    fetch: suspend () -> CityWeather?,
    saveFetchResult: suspend (CityWeather) -> Unit
): Flow<Resource<CityWeather?>> =
    flow {

        emit(Resource.Loading())

        val data = query()
        if (data != null) emit(Resource.Success(data))//Emit the cached data.
        try {
            val result = fetch()//Always an update of the weather is performed.
            if (result == null) emit(Resource.Error(data = data, messageResource = R.string.resource_not_found_error))
            else {
                saveFetchResult(result)
                emit(Resource.Success(query()))//Emit the new data storage locally.
            }
        } catch (e: HttpException) {
            emit(Resource.Error(data = data, messageResource = R.string.http_error))
        } catch (e: IOException) {
            emit(Resource.Error(data = data, messageResource = R.string.IO_error))
        }
    }


/**
 * This function create a workflow which request the cached data, verify if an update is needed,
 * request an update and save the result.
 * Trought this function the coordinates objects are provided to the repository.
 *
 * @param[query] a query to obtain the cached data. This is the selected
 * SINGLE SOURCE OF TRUTH of the data.
 * @param[fetch] a query to retrieve the updates from the network.
 * @param[saveFetchResult] a lambda that save the network retrieved data.
 *
 * @return a Flow of coordinates object resources that show the state of the data (data could be Loading, Success and Error)
 */
fun getNetworkBoundCoordinatesResource(
    query: suspend () -> Coordinates?,
    fetch: suspend () -> Coordinates?,
    saveFetchResult: suspend (Coordinates) -> Unit
): Flow<Resource<Coordinates?>> =
    flow {

        emit(Resource.Loading())

        val data = query()
        if (data != null) emit(Resource.Success(data))//Emit the cached coordinates and
        //stop because there is no need to update the found city coordinates.
        else {
            try {
                val result = fetch()
                if (result == null) emit(Resource.Error(R.string.resource_not_found_error))
                else {
                    saveFetchResult(result)
                    emit(Resource.Success(query()))//Emit the new data storage locally.
                }
            } catch (e: HttpException) {
                emit(Resource.Error(R.string.http_error))
            } catch (e: IOException) {
                emit(Resource.Error(R.string.IO_error))
            }
        }
    }