package com.musalatask.weatherapp.framework.retrofit.dto

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("1h") val lastHour: Double?,
    @SerializedName("3h") val last3Hour: Double?
)