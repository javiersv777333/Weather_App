package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.model.Coordinates
import com.musalatask.weatherapp.data.repository.FakeCityWeatherRepository
import com.musalatask.weatherapp.data.repository.FakeGeocodingRepository
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DeleteCityTest {

    private lateinit var deleteCity: DeleteCity
    private lateinit var fakeCityWeatherRepository: FakeCityWeatherRepository
    private lateinit var fakeGeocodingRepository: FakeGeocodingRepository

    @Before
    fun setUp() {
        fakeCityWeatherRepository = FakeCityWeatherRepository()
        fakeGeocodingRepository = FakeGeocodingRepository()
        deleteCity = DeleteCity(
            cityWeatherRepository = fakeCityWeatherRepository,
            geocodingRepository = fakeGeocodingRepository
        )

        populateFakeGeocodingRepository()
        populateFakeCityWeatherRepository()
    }

    @Test
    fun delete_city_correct_use_case() = runBlocking {

        assertTrue("Initial count of items in GeocodingRepository is 4",
            fakeGeocodingRepository.coordinates.size == 4)
        assertTrue("Initial count of items in CityWeatherRepository is 4",
            fakeCityWeatherRepository.cityWeathers.size == 4)

        deleteCity("Oslo")

        assertTrue("Count of items in GeocodingRepository after deleting is 3",
            fakeGeocodingRepository.coordinates.size == 3)
        assertTrue("Count of items in CityWeatherRepository after deleting is 3",
            fakeCityWeatherRepository.cityWeathers.size == 3)

        deleteCity("Oslo")

        assertTrue("Trying to delete the same city not affect the current items in GeocodingRepository",
            fakeGeocodingRepository.coordinates.size == 3)
        assertTrue("Trying to delete the same city not affect the current items in CityWeatherRepository",
            fakeCityWeatherRepository.cityWeathers.size == 3)
    }

    private fun populateFakeGeocodingRepository(){
        fakeGeocodingRepository.coordinates.add(
            Coordinates(
                latitude = 59.9133301,
                longitude = 10.7389701,
                cityName = "Oslo"
            )
        )
        fakeGeocodingRepository.coordinates.add(
            Coordinates(
                latitude = 43.6534817,
                longitude = -79.3839347,
                cityName = "Toronto"
            )
        )
        fakeGeocodingRepository.coordinates.add(
            Coordinates(
                latitude = 34.065846,
                longitude = -117.6484304,
                cityName = "Ontario"
            )
        )
        fakeGeocodingRepository.coordinates.add(
            Coordinates(
                latitude = 42.6977028,
                longitude = 23.3217359,
                cityName = "Sofia"
            )
        )
    }
    private fun populateFakeCityWeatherRepository(){
        fakeCityWeatherRepository.cityWeathers.add(
            CityWeather(
                description = "broken clouds",
                iconId = "04n",
                temp = 284.51,
                feelsLike = 283.89,
                tempMin = 283.97,
                tempMax =  284.96,
                pressure = 1019,
                humidity = 84,
                visibility = 10000,
                windSpeed = 2.06,
                windDirection = 160,
                cloudiness = 75,
                rainVolumeLastHour = null,
                snowVolumeLastHour = null,
                sunrise = 1667711196,
                sunset = 1667747644,
                timezone = 7200,
                cityName = "Oslo",
                lastUpdated = null,
                latitude = 59.9133301,
                longitude = 10.7389701,
                coordinatesName = null
            )
        )
        fakeCityWeatherRepository.cityWeathers.add(
            CityWeather(
                description = "broken clouds",
                iconId = "04n",
                temp = 284.51,
                feelsLike = 283.89,
                tempMin = 283.97,
                tempMax =  284.96,
                pressure = 1019,
                humidity = 84,
                visibility = 10000,
                windSpeed = 2.06,
                windDirection = 160,
                cloudiness = 75,
                rainVolumeLastHour = null,
                snowVolumeLastHour = null,
                sunrise = 1667711196,
                sunset = 1667747644,
                timezone = 7200,
                cityName = "Toronto",
                lastUpdated = null,
                latitude = 43.6534817,
                longitude = -79.3839347,
                coordinatesName = null
            )
        )
        fakeCityWeatherRepository.cityWeathers.add(
            CityWeather(
                description = "broken clouds",
                iconId = "04n",
                temp = 284.51,
                feelsLike = 283.89,
                tempMin = 283.97,
                tempMax =  284.96,
                pressure = 1019,
                humidity = 84,
                visibility = 10000,
                windSpeed = 2.06,
                windDirection = 160,
                cloudiness = 75,
                rainVolumeLastHour = null,
                snowVolumeLastHour = null,
                sunrise = 1667711196,
                sunset = 1667747644,
                timezone = 7200,
                cityName = "Ontario",
                lastUpdated = null,
                latitude = 34.065846,
                longitude = -117.6484304,
                coordinatesName = null
            )
        )
        fakeCityWeatherRepository.cityWeathers.add(
            CityWeather(
                description = "broken clouds",
                iconId = "04n",
                temp = 284.51,
                feelsLike = 283.89,
                tempMin = 283.97,
                tempMax =  284.96,
                pressure = 1019,
                humidity = 84,
                visibility = 10000,
                windSpeed = 2.06,
                windDirection = 160,
                cloudiness = 75,
                rainVolumeLastHour = null,
                snowVolumeLastHour = null,
                sunrise = 1667711196,
                sunset = 1667747644,
                timezone = 7200,
                cityName = "Sofia",
                lastUpdated = null,
                latitude = 42.6977028,
                longitude = 23.3217359,
                coordinatesName = null
            )
        )
    }
}