package com.musalatask.weatherapp.framework.utils

import android.text.format.DateUtils.isToday
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    fun formatTime(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    fun formatDateTime(dateTime: DateTime): String {
        val suffixes = arrayOf(
            "th",
            "st",
            "nd",
            "rd",
            "th",
            "th",
            "th",
            "th",
            "th",
            "th",  //    10    11    12    13    14    15    16    17    18    19
            "th",
            "th",
            "th",
            "th",
            "th",
            "th",
            "th",
            "th",
            "th",
            "th",  //    20    21    22    23    24    25    26    27    28    29
            "th",
            "st",
            "nd",
            "rd",
            "th",
            "th",
            "th",
            "th",
            "th",
            "th",  //    30    31
            "th",
            "st"
        )
        val df1 = SimpleDateFormat("E, MMMM dd", Locale.getDefault())
        val df2 = SimpleDateFormat(", y", Locale.getDefault())
        return df1.format(dateTime.millis) +
                suffixes[dateTime.dayOfMonth] +
                df2.format(dateTime.millis) +
                " at " +
                formatTime(dateTime.millis)
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    fun formatDate(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
        return dateFormat.format(timeInMillis)
    }

    /**
     * Returns whether the given date is yesterday, based on the user's current locale.
     */
    fun isYesterday(timeInMillis: Long): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val yesterdayMillis = DateTime.now().minusDays(1).millis
        val date = dateFormat.format(timeInMillis)
        return date == dateFormat.format(yesterdayMillis)
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    fun formatDateToStatus(timeInMillis: Long): String {
        if (isToday(timeInMillis)) {
            return "today at ${formatTime(timeInMillis)}"
        } else if (isYesterday(timeInMillis)) {
            return "yesterday at ${formatTime(timeInMillis)}"
        }
        return "${formatDate(timeInMillis)} at ${formatTime(timeInMillis)}"
    }

    fun getElapseTime(pastDate: DateTime): String {
        val pastMinutes = pastDate.millis / 60000
        val nowMinutes = DateTime.now().millis / 60000
        val elapseMinutes = nowMinutes - pastMinutes
        return if (elapseMinutes < 1) "Right now"
        else if(elapseMinutes < 2) "One minute ago"
        else if(elapseMinutes < 60) "$elapseMinutes minutes ago"
        else if(elapseMinutes / 60 < 24) "${elapseMinutes / 24} hours ago"
        else "Since ${formatDateToStatus(pastDate.millis)}"
    }
}