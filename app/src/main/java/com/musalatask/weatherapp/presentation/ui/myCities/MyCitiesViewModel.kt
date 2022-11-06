package com.musalatask.weatherapp.presentation.ui.myCities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.musalatask.weatherapp.domain.use_case.DeleteCity
import com.musalatask.weatherapp.domain.use_case.GetMyCityNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCitiesViewModel @Inject constructor(
    private val getMyCityNames: GetMyCityNames,
    private val deleteCity: DeleteCity
) : ViewModel() {

    val items = getMyCityNames()

    fun deleteCityByName(cityName: String) {
        viewModelScope.launch {
            deleteCity(cityName)
        }
    }
}