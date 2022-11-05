package com.musalatask.weatherapp.framework.utils

import android.content.Context
import android.net.ConnectivityManager

object ConnectivityUtils {

    fun hasNetworkAvailable(context: Context): Boolean {
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        return (network != null)
    }
}