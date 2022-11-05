package com.musalatask.weatherapp.framework.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

object ActivityUtils {

    fun hideKeyBoard(activity: Activity){
        // Check if no view has focus:
        val view: View? = activity.currentFocus
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    fun showSnackBar(message: String, view: View, @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_LONG){
        val snackbar = Snackbar
            .make(view, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}