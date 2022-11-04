package com.musalatask.weatherapp.data.repositoryImpl

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.local.CoordinatesLocalDataSource
import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.data.remote.CoordinatesRemoteDataSource
import com.musalatask.weatherapp.data.utils.getNetworkBoundResource
import com.musalatask.weatherapp.domain.repository.GeocodingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(
    private val localDataSource: CoordinatesLocalDataSource,
    private val remoteDataSource: CoordinatesRemoteDataSource
) : GeocodingRepository {

    override fun getCoordinatesOfACity(cityName: String): Flow<Resource<Coordinates?>> =
        getNetworkBoundResource(
            query = { localDataSource.getCoordinatesOfACity(cityName) },
            fetch = { remoteDataSource.getCoordinatesOfACity(cityName) },
            saveFetchResult = {
                localDataSource.withTransaction {
                    localDataSource.insertCoordinates(it)
                }
            },
            shouldFetch = { it == null }
        )
}