package com.riviem.sunalarm.features.home.presentation.homescreen.models

import androidx.compose.ui.graphics.Color
import java.time.ZonedDateTime

data class AlarmUIModel(
    val id: String,
    val time: ZonedDateTime,
    val name: String,
    val isOn: Boolean,
    val days: List<Day> = weekDays,
    val color: Color,
    val createdTimestamp: ZonedDateTime = ZonedDateTime.now()
)

val weekDays = listOf(
    Day("M", "Monday", false),
    Day("T", "Tuesday", false),
    Day("W", "Wednesday", false),
    Day("T", "Thursday", false),
    Day("F", "Friday", false),
    Day("S", "Saturday", false),
    Day("S", "Sunday", false)
)