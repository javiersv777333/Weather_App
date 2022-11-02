package com.musalatask.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _isSplashShowing = MutableStateFlow(true)
    val isSplashShowing = _isSplashShowing.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            _isSplashShowing.emit(false)
        }
    }
}