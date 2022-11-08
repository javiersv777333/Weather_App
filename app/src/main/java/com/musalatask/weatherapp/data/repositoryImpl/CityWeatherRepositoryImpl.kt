package com.musalatask.weatherapp.data.repositoryImpl

import com.musalatask.weatherapp.R
import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.local.CityWeatherLocalDataSource
import com.musalatask.weatherapp.domain.model.CityWeather
import com.musalatask.weatherapp.domain.model.Coordinates
import com.musalatask.weatherapp.data.remote.CityWeatherRemoteDataSource
import com.musalatask.weatherapp.data.utils.getNetworkBoundWeatherResource
import com.musalatask.weatherapp.domain.repository.CityWeatherRepository
import com.musalatask.weatherapp.domain.repository.GeocodingRepository
import kotlinx.coroutines.flow.*
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * This repository handle a local and remote data source for city weather objects.
 * A [geocodingRepository] is needed to handle the coordinates for city weather
 * requests.
 */
class CityWeatherRepositoryImpl @Inject constructor(
    private val localDataSource: CityWeatherLocalDataSource,
    private val remoteDataSource: CityWeatherRemoteDataSource,
    private val geocodingRepository: GeocodingRepository
) : CityWeatherRepository {

    /**
     * Get a flow that emits resources of city weather objects with info about the state of
     * the request (can be Loading, Success and Error)
     *
     * @param[cityName] the name of the desired city.
     *
     * @return a flow of resource of the city weather object with the name provided.
     */
    override fun getCityWeather(cityName: String): Flow<Resource<CityWeather?>> =
        getBaseCityWeather(cityName = cityName)

    /**
     * Get a flow that emits resources of city weather objects with info about the state of
     * the request (can be Loading, Success and Error)
     *
     * @param[latitude] specific geographic latitude of the desired city.
     * @param[longitude] specific geographic longitude of the desired city.
     *
     * @return a flow of resource of the city weather object with the coordinates provided.
     */
    override fun getCityWeather(
        latitude: Double,
        longitude: Double
    ): Flow<Resource<CityWeather?>> =
        getBaseCityWeather(latitude = latitude, longitude = longitude)

    override fun getAllCityWeathers(): Flow<List<CityWeather>> =
        localDataSource.getAllCityWeathers()

    /**
     * Delete the city weather object which contains [cityName] as attribute.
     *
     * @param[cityName] the name of the city.
     */
    override suspend fun deleteCityWeather(cityName: String) {
        localDataSource.deleteCityWeather(cityName)
    }

    /**
     * For retrieve a city weather, is needed a city name or a latitude and longitude coordinates.
     * This function request a city weather selecting one of the both params info depending on which
     * is not null.
     */
    private fun getBaseCityWeather(
        latitude: Double? = null,
        longitude: Double? = null,
        cityName: String? = null
    ): Flow<Resource<CityWeather?>> =
        getBaseCoordinates(latitude = latitude, longitude = longitude, cityName = cityName)//Get the specified
            //city coordinates object.
            .flatMapConcat { coordinates -> //Obtain the city weather object such the coordinates object retrieved.
                when (coordinates) {
                    is Resource.Error -> flow { emit(Resource.Error(coordinates.messageResource!!)) }
                    is Resource.Success -> if (coordinates.data == null) flow {
                        emit(Resource.Error(R.string.resource_not_found_error))
                    } else getCityWeather( //
                        cityName = coordinates.data!!.cityName,
                        latitude = coordinates.data!!.latitude,
                        longitude = coordinates.data!!.longitude
                    )
                    else -> flow { emit(Resource.Loading()) }
                }
            }

    /**
     * For retrieve a city coordinates object defined by the backend,
     * is needed a city name or a latitude and longitude coordinates.
     * This function request a city coordinates object selecting one of the both params info
     * depending on which is not null.
     */
    private fun getBaseCoordinates(
        latitude: Double? = null,
        longitude: Double? = null,
        cityName: String? = null
    ): Flow<Resource<Coordinates?>> =
        (if (cityName != null) geocodingRepository.getCoordinatesOfACity(cityName)
        else geocodingRepository.getCoordinatesOfACity(
            latitude = latitude!!,
            longitude = longitude!!
        ))

    /**
     * This function get the desiere city weather from a cache ([localDataSource]) and from the network
     * ([remoteDataSource])
     */
    private fun getCityWeather(
        cityName: String,
        latitude: Double,
        longitude: Double
    ): Flow<Resource<CityWeather?>> =
        getNetworkBoundWeatherResource(
            query = { localDataSource.getCityWeather(cityName) },
            fetch = { remoteDataSource.getCityWeather(latitude = latitude, longitude = longitude) },
            saveFetchResult = { saveLocalACityWeather(coordinatesName = cityName, cityWeather = it) }
        )

    /**
     * This function has the responsibility of save the city weather object retrieved from network.
     */
    private suspend fun saveLocalACityWeather(coordinatesName: String, cityWeather: CityWeather) {
        val now = DateTime.now().millis
        cityWeather.lastUpdated = now //It is needed know the moment when the info was obtained, for the user will
        //have feedback about how old the data is.

        cityWeather.cityName = coordinatesName//Some times the city name of the coordinates object is different
        //than the city name of the city weather object resulting, but both pointing to the same, for that, save the
        //coordinates object name will set it as weather city name.

        localDataSource.withAsynchronousContext {
            localDataSource.insertCityWeather(cityWeather)
        }
    }

    /**
     * This function gets all city weather objects which its name contains [text]
     * as subsequence.
     *
     * @param[text] the subsequence for which you want to search.
     *
     * @return Flow of list of city weather found objects.
     **/
    override fun getCityWeathersByText(text: String): Flow<List<CityWeather>> =
        localDataSource.getCityWeathersByText(text)
}