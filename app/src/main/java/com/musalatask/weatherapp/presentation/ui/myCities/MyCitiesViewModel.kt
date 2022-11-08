package com.musalatask.weatherapp.presentation.ui.myCities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musalatask.weatherapp.domain.use_case.DeleteCity
import com.musalatask.weatherapp.domain.use_case.GetCitiesByText
import com.musalatask.weatherapp.domain.use_case.GetMyCityNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the cities screen, the section have
 * a listing of the cities which the user already requested its weather,
 * deleting a city which the user already requested its weather and
 * filter the list of cities by some text entered by the user functionalities
 * ([getMyCityNames], [deleteCity] and [getCitiesByText] use cases).
 */
@HiltViewModel
class MyCitiesViewModel @Inject constructor(
    private val getMyCityNames: GetMyCityNames,
    private val deleteCity: DeleteCity,
    private val getCitiesByText: GetCitiesByText
) : ViewModel() {

    //Job associated with the coroutine util for cancel it.
    private var job: Job? = null

    //Flow which emits the updated city list.
    val states = MutableStateFlow(MyCitiesUiState())

    init {
        job = viewModelScope.launch {
            states.emitAll(getMyCityNames().map { MyCitiesUiState(it) })
        }
    }

    /**
     * This is a ui action, executed when the user touch a delete icon.
     *
     * @param[cityName] the city name which the user wants to delete.
     */
    fun deleteCityByName(cityName: String) {
        viewModelScope.launch {
            deleteCity(cityName)
        }
    }

    /**
     * This is a ui action, executed when the user write text in search view.
     *
     * @param[text] text which the user wants to filter the list
     */
    fun searchByText(text: String) {
        job?.cancel()
        job = viewModelScope.launch {
            states.emitAll(getCitiesByText(text).map { MyCitiesUiState(it) })
        }
    }
}