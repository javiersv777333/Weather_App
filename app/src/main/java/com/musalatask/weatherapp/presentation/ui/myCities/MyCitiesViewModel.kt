package com.musalatask.weatherapp.presentation.ui.myCities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.musalatask.weatherapp.domain.use_case.DeleteCity
import com.musalatask.weatherapp.domain.use_case.GetCitiesByText
import com.musalatask.weatherapp.domain.use_case.GetMyCityNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCitiesViewModel @Inject constructor(
    private val getMyCityNames: GetMyCityNames,
    private val deleteCity: DeleteCity,
    private val getCitiesByText: GetCitiesByText
) : ViewModel() {

    private var job: Job? = null

    val items = MutableStateFlow(listOf<String>())

    init {
        job = viewModelScope.launch {
            items.emitAll(getMyCityNames())
        }
    }

    fun deleteCityByName(cityName: String) {
        viewModelScope.launch {
            deleteCity(cityName)
        }
    }

    fun searchByText(text: String) {
        job?.cancel()
        job = viewModelScope.launch {
            items.emitAll(getCitiesByText(text))
        }
    }
}