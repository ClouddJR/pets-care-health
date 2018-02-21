package com.clouddroid.pettypetscarehealth.utils

/**
 * Created by arkadiusz on 20.02.18
 */

object DateUtils {

    fun formatDate(year: Int, month: Int, day: Int): String {
        val formattedMonth = if (month < 10) {
            "0${month + 1}"
        } else {
            "${month + 1}"
        }

        val formattedDay = if (day < 10) {
            "0$day"
        } else {
            "$day"
        }

        return "$year-$formattedMonth-$formattedDay"
    }

    fun formatTime(hour: Int, minute: Int): String {
        val formattedHour = if (hour < 10) {
            "0$hour"
        } else {
            "$hour"
        }

        val formattedMinutes = if (minute < 10) {
            "0$minute"
        } else {
            "$minute"
        }

        return "$formattedHour:$formattedMinutes"
    }

}