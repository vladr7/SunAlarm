package com.riviem.sunalarm.features.home.presentation.homescreen.models

data class AlarmUIModel(
    val id: Int,
    val time: String,
    val name: String,
    val isOn: Boolean,
    val days: List<DayUIModel> = weekDays
)

val weekDays = listOf(
    DayUIModel(
        day = Day(
            dayLetter = "M",
            dayFullName = "Monday"
        ),
        isSelected = false
    ),
       DayUIModel(
            day = Day(
                dayLetter = "T",
                dayFullName = "Tuesday"
            ),
            isSelected = false
        ),
        DayUIModel(
            day = Day(
                dayLetter = "W",
                dayFullName = "Wednesday"
            ),
            isSelected = false
        ),
        DayUIModel(
            day = Day(
                dayLetter = "T",
                dayFullName = "Thursday"
            ),
            isSelected = false
        ),
        DayUIModel(
            day = Day(
                dayLetter = "F",
                dayFullName = "Friday"
            ),
            isSelected = false
        ),
        DayUIModel(
            day = Day(
                dayLetter = "S",
                dayFullName = "Saturday"
            ),
            isSelected = false
        ),
        DayUIModel(
            day = Day(
                dayLetter = "S",
                dayFullName = "Sunday"
            ),
            isSelected = false
        ),
)