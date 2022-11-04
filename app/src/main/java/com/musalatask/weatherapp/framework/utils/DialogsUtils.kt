package com.musalatask.weatherapp.framework.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object DialogsUtils {

    fun createAlertDialog(
        context: Context,
        title: String = "Important!!",
        message: String,
        positiveButtonText: String? = null,
        positiveButtonAction: DialogInterface.OnClickListener? = null,
        negativeButtonText: String? = null,
        negativeButtonAction: DialogInterface.OnClickListener? = null
    ): AlertDialog {
        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
        if (!positiveButtonText.isNullOrBlank()) dialog.setPositiveButton(
            positiveButtonText,
            positiveButtonAction
        )
        if (!negativeButtonText.isNullOrBlank()) dialog.setNegativeButton(
            negativeButtonText,
            negativeButtonAction
        )
        return dialog.create()
    }
}