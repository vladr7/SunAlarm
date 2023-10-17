package com.riviem.sunalarm.features.home.presentation.models

data class AlarmUIModel(
    val id: Int,
    val time: String,
    val days: List<DayUIModel> = listOf(
        DayUIModel(
            day = "M",
            isSelected = true
        ),
        DayUIModel(
            day = "T",
            isSelected = true
        ),
        DayUIModel(
            day = "W",
            isSelected = false
        ),
        DayUIModel(
            day = "T",
            isSelected = false
        ),
        DayUIModel(
            day = "F",
            isSelected = true
        ),
        DayUIModel(
            day = "S",
            isSelected = true
        ),
        DayUIModel(
            day = "S",
            isSelected = false
        ),
    ),
    val isOn: Boolean
)