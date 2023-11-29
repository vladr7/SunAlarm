package com.riviem.sunalarm.features.home.data

import android.content.Context
import com.riviem.sunalarm.core.data.database.DatabaseAlarm
import com.riviem.sunalarm.core.presentation.enums.AlarmType
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.models.HourMinute
import com.riviem.sunalarm.features.settings.presentation.models.BrightnessSettingUI
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface AlarmRepository {

    fun getAlarms(): Flow<List<DatabaseAlarm>>

    fun getAlarmById(createdTimestampId: Int): DatabaseAlarm

    fun insertAll(alarms: List<DatabaseAlarm>)

    fun deleteAlarm(alarmId: Int, context: Context)

    fun deleteAllAlarms()

    fun insert(alarm: DatabaseAlarm)

    fun setAlarm(alarm: AlarmUIModel, context: Context)

    fun getNextAlarmDateTime(alarm: AlarmUIModel): ZonedDateTime

    suspend fun snoozeAlarm(alarm: AlarmUIModel, context: Context, alarmType: AlarmType)

    fun cancelLightAndSoundAlarm(alarmId: Int, context: Context)

    fun cancelAlarm(
        context: Context,
        alarmId: Int
    )

    suspend fun setSnoozeLength(snoozeLength: Int)

    suspend fun getSnoozeLength(): Int

    suspend fun setBrightnessSettings(brightnessSettingUI: BrightnessSettingUI)

    suspend fun getBrightnessSettings(): BrightnessSettingUI

    suspend fun getFirstDayOfWeek(): String

    suspend fun setFirstDayOfWeek(firstDayOfWeek: String)

    suspend fun setCurrentSoundAlarmIdForNotification(soundAlarmId: Int)

    suspend fun getCurrentSoundAlarmIdForNotification(): Int

    suspend fun setSunriseTime(sunriseTime: HourMinute)

    suspend fun getSunriseTime(): HourMinute

}