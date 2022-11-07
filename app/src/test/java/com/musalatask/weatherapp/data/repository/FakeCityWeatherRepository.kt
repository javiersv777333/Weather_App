package com.musalatask.weatherapp.data.repository

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCityWeatherRepository : CityWeatherRepository {

    val cityWeathers = mutableListOf<CityWeather>()

    override fun getCityWeather(cityName: String): Flow<Resource<CityWeather?>> {
        return flow {
            val cityWeather = cityWeathers.find { it.cityName == cityName || it.coordinatesName == cityName }
            emit(
                if (cityWeather == null) Resource.Error(6)
                else Resource.Success(cityWeather)
            )
        }
    }

    override fun getCityWeather(latitude: Double, longitude: Double): Flow<Resource<CityWeather?>> {
        return flow {
            val cityWeather = cityWeathers.find { it.latitude == latitude && it.longitude == longitude }
            emit(
                if (cityWeather == null) Resource.Error(6)
                else Resource.Success(cityWeather)
            )
        }
    }

    override fun getAllCityWeathers(): Flow<List<CityWeather>> {
        return flow { emit(cityWeathers) }
    }

    override suspend fun deleteCityWeather(cityName: String) {
        cityWeathers.removeIf { cityWeather -> cityWeather.cityName == cityName }
    }

    override fun getCityWeathersByText(text: String): Flow<List<CityWeather>> {
        return flow { cityWeathers.filter { it.cityName.lowercase().contains(text.lowercase()) ||
                it.coordinatesName != null && it.coordinatesName!!.lowercase().contains(text.lowercase()) } }
    }
}