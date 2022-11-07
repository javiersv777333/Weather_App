package com.musalatask.weatherapp.data.repository

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.domain.repository.GeocodingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGeocodingRepository : GeocodingRepository {

    val coordinates = mutableListOf<Coordinates>()

    override fun getCoordinatesOfACity(cityName: String): Flow<Resource<Coordinates?>> {
        return flow {
            val coordinates = coordinates.find { it.cityName == cityName }
            emit(
                if (coordinates == null) Resource.Error(6)
                else Resource.Success(coordinates)
            )
        }
    }

    override fun getCoordinatesOfACity(
        latitude: Double,
        longitude: Double
    ): Flow<Resource<Coordinates?>> {
        return flow {
            val coordinates = coordinates.find { it.latitude == latitude && it.longitude == longitude }
            emit(
                if (coordinates == null) Resource.Error(6)
                else Resource.Success(coordinates)
            )
        }
    }

    override suspend fun deleteCoordinates(cityName: String) {
        coordinates.removeIf { coordinates -> coordinates.cityName == cityName }
    }
}