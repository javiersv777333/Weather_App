package com.musalatask.weatherapp.presentation.ui.cityWeather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musalatask.weatherapp.common.Resource
import com.musalatask.weatherapp.data.model.CityWeather
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

@HiltViewModel
class CityWeatherViewModel @Inject constructor(
    private val getACityWeather: GetACityWeather,
    private val getMyCurrentCityWeather: GetMyCurrentCityWeather
) : ViewModel() {

    private var searchWeatherCityJob: Job? = null

    private val _uiState = MutableStateFlow(CityWeatherUiState())
    val uiState = _uiState as Flow<CityWeatherUiState>

    private val _currentTime = MutableStateFlow("")
    val currentTime = _currentTime as Flow<String>

    private val _lastUpdate = MutableStateFlow("")
    val lastUpdate = _lastUpdate as Flow<String>

    private var lastUpdateDateTime: DateTime? = null

    private var _lastSuccessCityName: String? = null
    val lastSuccessCityName: String?
        get() = _lastSuccessCityName

    var isSelectCityMenuItemVisible = true

    init {
        viewModelScope.launch {
            timerFlow(Duration.standardMinutes(1)).collect {
                _currentTime.update { (DateTimeUtils.formatDateTime(DateTime.now())) }
                if (lastUpdateDateTime != null) _lastUpdate.update {
                    DateTimeUtils.getElapseTime(
                        lastUpdateDateTime!!
                    )
                }
            }
        }
    }

    private fun timerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay.millis)
        while (true) {
            emit(DateTime.now())
            delay(period.millis)
        }
    }

    fun getCurrentCityWeather(latitude: Double, longitude: Double) =
        getCityWeather(latitude = latitude, longitude = longitude)

    fun getCityWeatherByName(cityName: String) =
        getCityWeather(cityName = cityName)

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

    private fun updateUiStateFromSuccessResource(resource: Resource.Success<CityWeather>) {
        _uiState.update {
            it.copy(
                isLoading = false,
                cityWeather = resource.data,
                errorMessage = null
            )
        }
        lastUpdateDateTime = resource.data?.lastUpdated?.let { DateTime(it) }
        _lastUpdate.update { DateTimeUtils.getElapseTime(lastUpdateDateTime!!) }
        _lastSuccessCityName = resource.data?.cityName
    }

    private fun updateUiStateFromLoadingResource(resource: Resource.Loading<CityWeather?>) {
        _uiState.update {
            it.copy(
                isLoading = resource.data == null,
                cityWeather = resource.data,
                errorMessage = null
            )
        }
        resource.data?.let {
            lastUpdateDateTime = it.lastUpdated?.let { DateTime(it) }
            _lastUpdate.update { DateTimeUtils.getElapseTime(lastUpdateDateTime!!) }
        }
    }

    private fun updateUiStateFromErrorResource(resource: Resource.Error<CityWeather?>) {
        _uiState.update {
            it.copy(
                errorMessage = resource.message
            )
        }
    }

    fun refreshWeather() {
        searchWeatherCityJob?.cancel()
        _lastSuccessCityName?.let {
            searchWeatherCityJob = viewModelScope.launch {
                getCityWeatherByName(it)
            }
        }
    }
}