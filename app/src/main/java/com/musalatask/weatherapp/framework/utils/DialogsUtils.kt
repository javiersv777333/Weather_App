package com.musalatask.weatherapp.framework.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.musalatask.weatherapp.R

object DialogsUtils {

    fun createAlertDialog(
        context: Context,
        title: String = context.getString(R.string.default_dialog_title),
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