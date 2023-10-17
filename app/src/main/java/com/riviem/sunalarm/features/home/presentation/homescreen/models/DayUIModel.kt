package com.riviem.sunalarm.features.home.presentation.homescreen.models

data class DayUIModel(
    val day: String,
    val isSelected: Boolean
)

fun List<DayUIModel>.allDoorsSelected(): Boolean {
    return this.all { it.isSelected }
}