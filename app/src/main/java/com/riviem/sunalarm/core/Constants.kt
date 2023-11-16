package com.riviem.sunalarm.core

object Constants {
    const val CREATED_TIMESTAMP_ID = "createdTimestamp"
    const val FROM_ALARM_ID = "fromAlarm"
    const val ALARM_TYPE_ID = "alarmType"
    const val CAMERA_REQUEST_CODE = 101
    const val DISMISS_SOUND_ALARM_NOTIFICATION_ID = 1001
    const val LOCATION_PERMISSION_REQUEST_CODE = 1002

    // Config values
    const val MINUTES_UNTIL_SOUND_ALARM_INITIAL_VALUE = 1
    const val MINUTES_UNTIL_SOUND_ALARM_INTERVAL = 121
    const val KEEP_LIGHT_SCREEN_ON_FOR_MINUTES  = 120 * 60 * 1000L
}
