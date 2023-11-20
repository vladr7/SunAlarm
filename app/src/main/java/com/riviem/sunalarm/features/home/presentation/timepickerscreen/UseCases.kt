package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import android.content.Context
import com.riviem.sunalarm.R
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day

fun updateModalTitle(days: List<Day>, context: Context): String {
    if (days.all { it.isSelected }) {
        return context.getString(R.string.every_day)
    }
    val selectedDays = days.filter { it.isSelected }

    val selectedDaysShortNames = selectedDays.map { day ->
        when (day.fullName) {
            "Monday" -> context.getString(R.string.monday_short)
            "Tuesday" -> context.getString(R.string.tuesday_short)
            "Wednesday" -> context.getString(R.string.wednesday_short)
            "Thursday" -> context.getString(R.string.thursday_short)
            "Friday" -> context.getString(R.string.friday_short)
            "Saturday" -> context.getString(R.string.saturday_short)
            "Sunday" -> context.getString(R.string.sunday_short)
            else -> ""
        }
    }

    return context.getString(R.string.every, selectedDaysShortNames.joinToString(", "))
}

