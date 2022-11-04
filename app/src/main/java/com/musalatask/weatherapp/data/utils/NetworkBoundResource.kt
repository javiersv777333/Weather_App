package com.musalatask.weatherapp.data.utils

import com.musalatask.weatherapp.common.Resource
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

fun <T : Any> getNetworkBoundResource(
    query: () -> Flow<T?>,
    fetch: suspend () -> T?,
    saveFetchResult: suspend (T) -> Unit,
    shouldFetch: (T?) -> Boolean = { true }
): Flow<Resource<T?>> =
    merge(
        query().map { Resource.Success(it) },
        flow {
            val data = query().first()
            if (shouldFetch(data)) {
                emit(Resource.Loading(data))

                try {
                    val result = fetch()
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
        }
    )