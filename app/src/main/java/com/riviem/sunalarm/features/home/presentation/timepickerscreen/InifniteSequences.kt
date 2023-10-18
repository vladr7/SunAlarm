package com.riviem.sunalarm.features.home.presentation.timepickerscreen

val infiniteHours = sequence {
    while (true) {
        yieldAll(1..24)
    }
}

val infiniteMinutes = sequence {
    while (true) {
        yieldAll(0..59)
    }
}