package com.riviem.sunalarm.core

import com.riviem.sunalarm.features.home.presentation.timepickerscreen.infiniteHours
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.infiniteMinutes

object Constants {
    const val CREATED_TIMESTAMP_ID = "createdTimestamp"
    const val FROM_ALARM_ID = "fromAlarm"
    const val ALARM_TYPE_ID = "alarmType"
    const val DISMISS_SOUND_ALARM_NOTIFICATION_ID = 1001

    // Config values
    const val MINUTES_UNTIL_SOUND_ALARM_INITIAL_VALUE = 20
    const val MINUTES_UNTIL_SOUND_ALARM_INTERVAL = 121
    const val SNOOZE_MAX_LENGTH_MINUTES = 61
    const val INCREASE_BRIGHTNESS_OVER_TIME_INTERVAL = 61
    const val DEFAULT_SUNRISE_TIME = "7:0"
    val hours = infiniteHours.take(480).map { if (it < 10) "0$it" else it.toString() }.toList()
    val minutes = infiniteMinutes.take(1200).map { if (it < 10) "0$it" else it.toString() }.toList()
    const val DEFAULT_BRIGHTNESS_GRADUAL_VALUE = 20
}
