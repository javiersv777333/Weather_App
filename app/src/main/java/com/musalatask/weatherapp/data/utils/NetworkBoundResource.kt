package com.musalatask.weatherapp.data.utils

import com.musalatask.weatherapp.common.Resource
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

fun <T : Any> getNetworkBoundWeatherResource(
    query: () -> Flow<T?>,
    fetch: suspend () -> T?,
    saveFetchResult: suspend (T) -> Unit
): Flow<Resource<T?>> =
    flow {

        emit(Resource.Loading())

        val data = query().first()
        if (data != null) emit(Resource.Loading(data))
        try {
            val result = fetch()
            if (result == null) emit(Resource.Error("Resource not found!"))
            else {
                saveFetchResult(result)
                emit(Resource.Success(query().first()))
            }
        } catch (e: HttpException) {
            emit(
                Resource.Error(
                    data = data,
                    message = "Oops, something went wrong!"
                )
            )
        } catch (e: IOException) {
            emit(
                Resource.Error(
                    data = data,
                    message = "Couldn't reach server, check your internet connection."
                )
            )
        }
    }

fun <T : Any> getNetworkBoundCoordinatesResource(
    query: () -> Flow<T?>,
    fetch: suspend () -> T?,
    saveFetchResult: suspend (T) -> Unit
): Flow<Resource<T?>> =
    flow {

        emit(Resource.Loading())

        val data = query().first()
        if (data != null) emit(Resource.Success(data))
        else {
            try {
                val result = fetch()
                if (result == null) emit(Resource.Error("Resource not found!"))
                else {
                    saveFetchResult(result)
                    emit(Resource.Success(query().first()))
                }
            } catch (e: HttpException) {
                emit(Resource.Error("Oops, something went wrong!"))
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server, check your internet connection."))
            }
        }
    }