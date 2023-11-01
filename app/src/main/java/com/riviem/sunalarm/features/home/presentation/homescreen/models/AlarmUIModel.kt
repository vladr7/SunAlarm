package com.riviem.sunalarm.features.home.presentation.homescreen.models

import androidx.compose.ui.graphics.Color
import java.time.ZonedDateTime

data class AlarmUIModel(
    val createdTimestamp: Int,
    val ringTime: ZonedDateTime,
    val name: String,
    val isOn: Boolean,
    val days: List<Day> = weekDays,
    val color: Color,
    val flashlight: Boolean,
)

val weekDays = listOf(
    Day("M", "Monday", true),
    Day("T", "Tuesday", true),
    Day("W", "Wednesday", true),
    Day("T", "Thursday", true),
    Day("F", "Friday", true),
    Day("S", "Saturday", true),
    Day("S", "Sunday", true)
)

fun getDayFromFullName(fullName: String): Day {
    return weekDays.first { day -> day.fullName == fullName }
}
