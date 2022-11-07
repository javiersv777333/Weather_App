package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.repository.FakeCityWeatherRepository
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetACityWeatherTest {

    private lateinit var getACityWeather: GetACityWeather
    private lateinit var fakeCityWeatherRepository: FakeCityWeatherRepository

    @Before
    fun setUp() {
        fakeCityWeatherRepository = FakeCityWeatherRepository()
        getACityWeather = GetACityWeather(fakeCityWeatherRepository)

        populateFakeCityWeatherRepository()
    }

    @Test
    fun get_exist_weather_by_city_name_correct_use_case() = runTest{

        val cityWeatherFlow = getACityWeather("Sofia")

        val collectJob = launch(UnconfinedTestDispatcher()){
            cityWeatherFlow.collect{
                assertTrue("The resource requested has to be a Success", it is Resource.Success)
                assertNotNull("The city weather requested cannot be null", it.data)
                assertTrue("The city weather retried was correct", it.data!!.cityName == "Sofia")
            }
        }

        collectJob.cancel()
    }

    @Test
    fun get_not_exist_weather_by_city_name_correct_use_case() = runTest{

        val cityWeatherFlow = getACityWeather("Havana")

        val collectJob = launch(UnconfinedTestDispatcher()){
            cityWeatherFlow.collect{
                assertTrue("The resource requested has to be a Error", it is Resource.Error)
                assertNull("The resource requested have to have data in null", it.data)
            }
        }

        collectJob.cancel()
    }

    @Test
    fun get_empty_flow_weather_by_city_empty_name_correct_use_case() = runTest{

        val cityWeatherFlow = getACityWeather("")
        assertTrue("The flow result have to be empty", cityWeatherFlow.toList().isEmpty())
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