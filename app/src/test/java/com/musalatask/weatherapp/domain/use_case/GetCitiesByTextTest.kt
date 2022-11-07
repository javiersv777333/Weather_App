package com.musalatask.weatherapp.domain.use_case

import com.musalatask.weatherapp.data.model.CityWeather
import com.musalatask.weatherapp.data.repository.FakeCityWeatherRepository
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class GetCitiesByTextTest {

    private lateinit var getCitiesByText: GetCitiesByText
    private lateinit var fakeCityWeatherRepository: FakeCityWeatherRepository

    @Before
    fun setUp() {

        fakeCityWeatherRepository = FakeCityWeatherRepository()
        getCitiesByText = GetCitiesByText(fakeCityWeatherRepository)

        populateFakeCityWeatherRepository()
    }

    @Test
    fun get_empty_flow_weather_by_not_contained_text_correct_use_case() = runTest{

        val cityWeatherFlow = getCitiesByText("www")
        TestCase.assertTrue("The flow result have to be empty", cityWeatherFlow.toList().isEmpty())
    }

    @Test
    fun get_full_flow_weather_by_text_correct_use_case() = runTest{

        val collectJob = launch(UnconfinedTestDispatcher()) {
            getCitiesByText("").collect {
                TestCase.assertTrue("The flow result have to have all items", it.size == 4)
            }
        }
        collectJob.cancel()
    }

    @Test
    fun get_flow_weather_by_text_correct_use_case() = runTest{

        val collectJob = launch(UnconfinedTestDispatcher()) {
            getCitiesByText("on").collect {
                TestCase.assertTrue("The flow result have to have 2 items", it.size == 2)
                TestCase.assertTrue("The flow result have to have Toronto and Ontario",
                    it.contains("Toronto") && it.contains("Ontario"))
            }
        }
        collectJob.cancel()
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