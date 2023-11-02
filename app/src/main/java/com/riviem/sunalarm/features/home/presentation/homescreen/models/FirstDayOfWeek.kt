package com.riviem.sunalarm.features.home.presentation.homescreen.models

enum class FirstDayOfWeek(val fullName: String) {
    MONDAY("Monday"),
    SUNDAY("Sunday");

    companion object {
        fun getFirstDayOfWeek(fullName: String): FirstDayOfWeek {
            return when (fullName) {
                MONDAY.fullName -> MONDAY
                SUNDAY.fullName -> SUNDAY
                else -> MONDAY
            }
        }
    }
}