package com.musalatask.weatherapp.data.repositoryImpl

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.local.CoordinatesLocalDataSource
import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.data.remote.CoordinatesRemoteDataSource
import com.musalatask.weatherapp.data.utils.getNetworkBoundCoordinatesResource
import com.musalatask.weatherapp.domain.repository.GeocodingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GeocodingRepositoryImpl @Inject constructor(
    private val localDataSource: CoordinatesLocalDataSource,
    private val remoteDataSource: CoordinatesRemoteDataSource
) : GeocodingRepository {

    override fun getCoordinatesOfACity(cityName: String): Flow<Resource<Coordinates?>> =
        getNetworkBoundCoordinatesResource(
            query = { localDataSource.getCoordinatesOfACity(cityName) },
            fetch = { remoteDataSource.getCoordinatesOfACity(cityName) },
            saveFetchResult = {
                localDataSource.withTransaction {
                    localDataSource.insertCoordinates(it)
                }
            }
        )

    override fun getCoordinatesOfACity(latitude: Double, longitude: Double): Flow<Resource<Coordinates?>> =
        flow {
            emit(Resource.Loading())
            val coordinates = remoteDataSource.getCoordinates(latitude = latitude, longitude = longitude)
            coordinates?.let {
                localDataSource.withTransaction {
                    localDataSource.insertCoordinates(it)
                }
                emit(Resource.Success(localDataSource.getCoordinatesOfACity(coordinates.cityName)))
            } ?: emit(Resource.Error("Resource not found!"))
        }
}