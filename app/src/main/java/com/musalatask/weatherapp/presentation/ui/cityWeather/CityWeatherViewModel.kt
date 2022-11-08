package com.musalatask.weatherapp.presentation.ui.cityWeather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musalatask.weatherapp.R
import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.domain.model.CityWeather
import com.musalatask.weatherapp.domain.use_case.GetACityWeather
import com.musalatask.weatherapp.domain.use_case.GetMyCurrentCityWeather
import com.musalatask.weatherapp.framework.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.Duration
import javax.inject.Inject

/**
 * ViewModel for the city weather screen, the section have
 * a request city weather by name and request weather for the current
 * coordinates location functionalities ([getCityWeatherByName] and [getCurrentCityWeather] uses cases).
 */
@HiltViewModel
class CityWeatherViewModel @Inject constructor(
    private val getACityWeather: GetACityWeather,
    private val getMyCurrentCityWeather: GetMyCurrentCityWeather
) : ViewModel() {

    //Job associated with the coroutine util for cancel it.
    private var searchWeatherCityJob: Job? = null

    //Flow that emits all ui states updates.
    private val _uiState = MutableStateFlow(CityWeatherUiState())
    val uiState = _uiState as Flow<CityWeatherUiState>

    //Flow that emits the current time.
    private val _currentTime = MutableStateFlow("")
    val currentTime = _currentTime as Flow<String>

    //Flow that emits the time since last time in which the specified weather
    //was updated.
    private val _lastUpdate = MutableStateFlow("")
    val lastUpdate = _lastUpdate as Flow<String>

    private var lastUpdateDateTime: DateTime? = null

    //The name of the last city whose weather was requested.
    private var _lastCityNameRequested: String? = null
    val lastCityNameRequested: String?
        get() = _lastCityNameRequested

    private var currentGeographicalCoordinates: Pair<Double, Double>? = null

    //State of the selectCityMenuItem visibility.
    var isSelectCityMenuItemVisible = true

    init {
        viewModelScope.launch {
            timerFlow(Duration.standardMinutes(1)).collect {

                //Update the current time and the time since last updated.
                _currentTime.update { (DateTimeUtils.formatDateTime(DateTime.now())) }
                if (lastUpdateDateTime != null) _lastUpdate.update {
                    DateTimeUtils.getElapseTime(
                        lastUpdateDateTime!!
                    )
                }
            }
        }
    }

    /**
     * This create a timer using a kotlin flows.
     */
    private fun timerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay.millis)
        while (true) {
            emit(DateTime.now())
            delay(period.millis)
        }
    }

    /**
     * This is a ui action, executed when the user launch the app.
     *
     * @param[latitude] the current latitude of the device.
     * @param[longitude] the current longitude of the device.
     */
    fun getCurrentCityWeather(latitude: Double, longitude: Double) {
        currentGeographicalCoordinates = Pair(latitude, longitude)
        getCityWeather(latitude = latitude, longitude = longitude)
    }

    /**
     * This is a ui action, executed when the user submit a city name to request
     * its weather.
     *
     * @param[cityName] name of the city which the user wants to know its weather.
     */
    fun getCityWeatherByName(cityName: String) {
        _lastCityNameRequested = cityName//Save the city name that the user wants to request
        //for the case that the user wants tu refresh the request.
        getCityWeather(cityName = cityName)
    }

    /**
     * For retrieve a city weather, is needed a city name or a latitude and longitude coordinates.
     * This function request a city weather selecting one of the both params info depending on which
     * is not null and create a new ui state with the result requested.
     */
    private fun getCityWeather(
        latitude: Double? = null,
        longitude: Double? = null,
        cityName: String? = null
    ) {
        searchWeatherCityJob?.cancel()

        val flow = if (cityName != null) getACityWeather(cityName)
        else getMyCurrentCityWeather(latitude = latitude!!, longitude = longitude!!)

        searchWeatherCityJob = viewModelScope.launch {
            flow.onEach { cityWeatherResource ->
                when (cityWeatherResource) {
                    is Resource.Success -> {
                        updateUiStateFromSuccessResource(cityWeatherResource as Resource.Success<CityWeather>)
                    }
                    is Resource.Loading -> {
                        updateUiStateFromLoadingResource(cityWeatherResource)
                    }
                    is Resource.Error -> {
                        updateUiStateFromErrorResource(cityWeatherResource)
                    }
                }
            }.launchIn(this)
        }
    }

    /**
     * This create a new ui state from a success city weather resource.
     *
     * @param[resource] a success resource of city weather object.
     */
    private fun updateUiStateFromSuccessResource(resource: Resource.Success<CityWeather>) {
        _uiState.update {
            it.copy(
                isLoading = false,
                cityWeather = resource.data,
                errorResource = null
            )
        }
        lastUpdateDateTime = resource.data?.lastUpdated?.let { DateTime(it) }
        _lastUpdate.update { DateTimeUtils.getElapseTime(lastUpdateDateTime!!) }
        _lastCityNameRequested = resource.data?.cityName
    }

    /**
     * This create a new ui state from a loading city weather resource.
     *
     * @param[resource] a loading resource of city weather object.
     */
    private fun updateUiStateFromLoadingResource(resource: Resource.Loading<CityWeather?>) {
        _uiState.update {
            it.copy(
                isLoading = resource.data == null,
                cityWeather = resource.data,
                errorResource = null
            )
        }
        resource.data?.let {
            lastUpdateDateTime = it.lastUpdated?.let { DateTime(it) }
            _lastUpdate.update { DateTimeUtils.getElapseTime(lastUpdateDateTime!!) }
        }
    }

    /**
     * This create a new ui state from a error city weather resource.
     *
     * @param[resource] a error resource of city weather object.
     */
    private fun updateUiStateFromErrorResource(resource: Resource.Error<CityWeather?>) {
        _uiState.update {
            it.copy(
                errorResource = resource.messageResource
            )
        }
    }

    /**
     * This is a ui action, executed when the user pull to refresh. This update
     * the last successfully city weather requested.
     */
    fun refreshWeather() {
        searchWeatherCityJob?.cancel()
        _lastCityNameRequested?.let {
            searchWeatherCityJob = viewModelScope.launch {
                getCityWeatherByName(it)
            }
        } ?: currentGeographicalCoordinates?.let { //This happens when the automatic weather request
            // fails and the user pull to refresh.
            searchWeatherCityJob = viewModelScope.launch {
                getCurrentCityWeather(it.first, it.second)
            }
        }
    }
}