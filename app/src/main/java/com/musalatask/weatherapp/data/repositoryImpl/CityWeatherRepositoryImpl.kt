package com.musalatask.weatherapp.data.repositoryImpl

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.local.CityWeatherLocalDataSource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.remote.CityWeatherRemoteDataSource
import com.musalatask.weatherapp.data.utils.getNetworkBoundWeatherResource
import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import com.musalatask.weatherapp.domain.repository.GeocodingRepository
import kotlinx.coroutines.flow.*
import org.joda.time.DateTime
import javax.inject.Inject

class CityWeatherRepositoryImpl @Inject constructor(
    private val localDataSource: CityWeatherLocalDataSource,
    private val remoteDataSource: CityWeatherRemoteDataSource,
    private val geocodingRepository: GeocodingRepository
) : CityWeatherRepository {

    override fun getCityWeather(cityName: String): Flow<Resource<CityWeather?>> =
        geocodingRepository.getCoordinatesOfACity(cityName)
            .flatMapConcat { coordinates ->
                when (coordinates) {
                    is Resource.Error -> flow { emit(Resource.Error(coordinates.message!!)) }
                    is Resource.Success -> if (coordinates.data == null) flow {
                        emit(Resource.Success(null))
                    } else getCityWeather(
                        cityName = cityName,
                        latitude = coordinates.data!!.latitude,
                        longitude = coordinates.data!!.longitude
                    )
                    else -> flow { emit(Resource.Loading()) }
                }
            }

    private fun getCityWeather(
        cityName: String,
        latitude: Double,
        longitude: Double
    ): Flow<Resource<CityWeather?>> =
        getNetworkBoundWeatherResource(
            query = { localDataSource.getCityWeather(cityName) },
            fetch = { remoteDataSource.getCityWeather(latitude = latitude, longitude = longitude) },
            saveFetchResult = { saveLocalACityWeather(it) }
        )

    override fun getCityWeather(
        latitude: Double,
        longitude: Double
    ): Flow<Resource<CityWeather?>> =
        geocodingRepository.getCoordinatesOfACity(latitude = latitude, longitude = longitude)
            .flatMapConcat { coordinates ->
                when (coordinates) {
                    is Resource.Error -> flow { emit(Resource.Error(coordinates.message!!)) }
                    is Resource.Success -> if (coordinates.data == null) flow {
                        emit(Resource.Success(null))
                    } else getCityWeather(
                        cityName = coordinates.data!!.cityName,
                        latitude = coordinates.data!!.latitude,
                        longitude = coordinates.data!!.longitude
                    )
                    else -> flow { emit(Resource.Loading()) }
                }
            }

    private suspend fun saveLocalACityWeather(cityWeather: CityWeather) {
        val now = DateTime.now().millis
        cityWeather.lastUpdated = now
        localDataSource.withTransaction {
            localDataSource.insertCityWeather(cityWeather)
        }
    }
}