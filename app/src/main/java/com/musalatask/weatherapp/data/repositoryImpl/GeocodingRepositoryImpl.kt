package com.musalatask.weatherapp.data.repositoryImpl

import com.musalatask.weatherapp.R
import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.local.CoordinatesLocalDataSource
import com.musalatask.weatherapp.domain.model.Coordinates
import com.musalatask.weatherapp.data.remote.CoordinatesRemoteDataSource
import com.musalatask.weatherapp.data.utils.getNetworkBoundCoordinatesResource
import com.musalatask.weatherapp.domain.repository.GeocodingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * This repository handle a local and remote data source for city coordinates objects.
 */
class GeocodingRepositoryImpl @Inject constructor(
    private val localDataSource: CoordinatesLocalDataSource,
    private val remoteDataSource: CoordinatesRemoteDataSource
) : GeocodingRepository {

    /**
     * Get a flow that emits resources of city coordinates objects with info about the state of
     * the request (can be Loading, Success and Error)
     *
     * @param[cityName] the name of the desired city.
     *
     * @return a flow of resource of the city coordinates object with the name provided.
     */
    override fun getCoordinatesOfACity(cityName: String): Flow<Resource<Coordinates?>> =
        getNetworkBoundCoordinatesResource(
            query = { localDataSource.getCoordinatesOfACity(cityName) },
            fetch = { remoteDataSource.getCoordinatesOfACity(cityName) },
            saveFetchResult = {
                localDataSource.withAsynchronousContext {
                    if (!it.names.contains(cityName))
                        it.names.add(cityName)//This is used to save the name
                    //used for the user to find this coordinates.
                    localDataSource.insertCoordinates(it)
                }
            }
        )

    /**
     * Get a flow that emits resources of city coordinates objects with info about the state of
     * the request (can be Loading, Success and Error). This function is used to request a coordinates object
     * given the geographical coordinates that the user has.
     *
     * @param[latitude] the geographic latitude.
     * @param[longitude] the geographic longitude.
     *
     * @returnGet a flow of resource of the city coordinates object with the geographical coordinates provided.
     */
    override fun getCoordinatesOfACity(latitude: Double, longitude: Double): Flow<Resource<Coordinates?>> =
        flow {
            emit(Resource.Loading())
            val coordinates = remoteDataSource.getCoordinates(latitude = latitude, longitude = longitude)
            coordinates?.let {
                localDataSource.withAsynchronousContext {
                    localDataSource.insertCoordinates(it)
                }
                emit(Resource.Success(localDataSource.getCoordinatesOfACity(coordinates.cityName)))
            } ?: emit(Resource.Error(R.string.resource_not_found_error))
        }

    /**
     * Delete the city coordinates object which contains [cityName] as attribute.
     *
     * @param[cityName] the name of the city.
     */
    override suspend fun deleteCoordinates(cityName: String) {
        localDataSource.deleteCoordinates(cityName)
    }
}