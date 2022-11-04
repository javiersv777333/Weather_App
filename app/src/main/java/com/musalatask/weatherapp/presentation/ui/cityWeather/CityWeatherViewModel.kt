package com.musalatask.weatherapp.presentation.ui.cityWeather

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musalatask.weatherapp.domain.use_case.GetACityWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityWeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getACityWeather: GetACityWeather
) : ViewModel() {

    private val _isSplashShowing = MutableStateFlow(true)
    val isSplashShowing = _isSplashShowing.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _isSplashShowing.emit(false)
        }
    }

//    private val _searchQuery = MutableSharedFlow<>()
}