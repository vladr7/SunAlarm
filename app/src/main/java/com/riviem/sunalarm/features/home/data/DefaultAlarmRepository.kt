package com.riviem.sunalarm.features.home.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.riviem.sunalarm.AlarmReceiver
import com.riviem.sunalarm.core.Constants
import com.riviem.sunalarm.core.data.database.AlarmDatabase
import com.riviem.sunalarm.core.data.database.DatabaseAlarm
import com.riviem.sunalarm.core.data.local.LocalStorage
import com.riviem.sunalarm.core.data.local.LocalStorageKeys
import com.riviem.sunalarm.core.presentation.enums.AlarmType
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.weekDays
import com.riviem.sunalarm.features.settings.presentation.models.BrightnessSettingUI
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject


class DefaultAlarmRepository @Inject constructor(
    private val alarmDatabase: AlarmDatabase,
    private val localStorage: LocalStorage
) : AlarmRepository {

    override fun getAlarms(): Flow<List<DatabaseAlarm>> {
        return alarmDatabase.alarmDao.getAlarms()
    }

    override fun getAlarmById(createdTimestampId: Int): DatabaseAlarm {
        return alarmDatabase.alarmDao.getAlarmById(createdTimestampId)
    }

    override fun insertAll(alarms: List<DatabaseAlarm>) {
        alarmDatabase.alarmDao.insertAll(alarms)
    }

    override fun deleteAlarm(alarmId: Int) {
        alarmDatabase.alarmDao.deleteAlarm(alarmId)
    }

    override fun deleteAllAlarms() {
        alarmDatabase.alarmDao.deleteAllAlarms()
    }

    override fun insert(alarm: DatabaseAlarm) {
        alarmDatabase.alarmDao.insert(alarm)
    }

    override fun getNextAlarmDateTime(alarm: AlarmUIModel): ZonedDateTime {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val currentDayOfWeek = now.dayOfWeek.value - 1 // 1 = Monday, 7 = Sunday

        val currentHour = now.hour
        val currentMinute = now.minute
        val currentTimeInMinutes = currentHour * 60 + currentMinute
        val alarmTimeInMinutes = alarm.ringTime.hour * 60 + alarm.ringTime.minute

        val daysToNextAlarm =
            if (alarm.days[currentDayOfWeek].isSelected && currentTimeInMinutes <= alarmTimeInMinutes - 1) {
                0L // Set alarm for today
            } else {
                // Find the next selected day after today
                val daysAfterCurrent =
                    (alarm.days.drop(currentDayOfWeek + 1) + alarm.days.take(currentDayOfWeek + 1))
                daysAfterCurrent.indexOfFirst { it.isSelected } + 1L
            }

        return now
            .withHour(alarm.ringTime.hour)
            .withMinute(alarm.ringTime.minute)
            .withSecond(0)
            .plusDays(daysToNextAlarm)
    }

    override fun setLightAlarm(alarm: AlarmUIModel, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmDateTime = getNextAlarmDateTime(alarm)

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constants.CREATED_TIMESTAMP_ID, alarm.createdTimestamp)
        intent.putExtra(Constants.ALARM_TYPE_ID, AlarmType.LIGHT.name)

        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.createdTimestamp, intent, PendingIntent.FLAG_IMMUTABLE
        )

        println("vladlog: setLightAlarm for: $alarmDateTime with id: ${alarm.createdTimestamp}")

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarmDateTime.toInstant().toEpochMilli(),
            pendingIntent
        )

        if (alarm.soundAlarmEnabled) {
            setSoundAlarm(alarm, alarmDateTime, context)
        }
    }

    private fun setSoundAlarm(alarm: AlarmUIModel, alarmDateTime: ZonedDateTime, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val id = alarm.createdTimestamp + 1
        val updatedAlarmDateTime = alarmDateTime.plusMinutes(alarm.minutesUntilSoundAlarm.toLong())

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constants.CREATED_TIMESTAMP_ID, id)
        intent.putExtra(Constants.ALARM_TYPE_ID, AlarmType.SOUND.name)

        val pendingIntent = PendingIntent.getBroadcast(
            context, id, intent, PendingIntent.FLAG_IMMUTABLE
        )

        println("vladlog: setSoundAlarm for: $updatedAlarmDateTime with id: $id")

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            updatedAlarmDateTime.toInstant().toEpochMilli(),
            pendingIntent
        )
    }

    override suspend fun snoozeAlarm(alarm: AlarmUIModel, context: Context, alarmType: AlarmType) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val id = if (alarmType == AlarmType.LIGHT) {
            alarm.createdTimestamp
        } else {
            alarm.createdTimestamp + 1
        }

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constants.CREATED_TIMESTAMP_ID, id)
        intent.putExtra(Constants.ALARM_TYPE_ID, alarmType.name)

        val pendingIntent = PendingIntent.getBroadcast(
            context, id, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeLength = localStorage.getInt(LocalStorageKeys.SNOOZE_LENGTH_KEY, 5)
        val snoozeDateTime =
            ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(snoozeLength.toLong())

        println("vladlog: snoozeAlarm: $snoozeDateTime with id: ${id} type: $alarmType")

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            snoozeDateTime.toInstant().toEpochMilli(),
            pendingIntent
        )
    }


    override fun cancelLightAndSoundAlarm(alarm: AlarmUIModel, context: Context) {
        cancelLightAlarm(context, alarm)
        cancelSoundAlarm(context, alarm.createdTimestamp + 1)
    }

    private fun cancelLightAlarm(
        context: Context,
        alarm: AlarmUIModel
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.createdTimestamp, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        println("vladlog: cancelLightAlarm: ${alarm.createdTimestamp}")
        alarmManager.cancel(pendingIntent)
    }

    override fun cancelSoundAlarm(
        context: Context,
        soundAlarmId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, soundAlarmId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        println("vladlog: cancelSoundAlarm: ${soundAlarmId}")
        alarmManager.cancel(pendingIntent)
    }

    override suspend fun setSnoozeLength(snoozeLength: Int) {
        localStorage.putInt(LocalStorageKeys.SNOOZE_LENGTH_KEY, snoozeLength)
    }

    override suspend fun getSnoozeLength(): Int {
        return localStorage.getInt(LocalStorageKeys.SNOOZE_LENGTH_KEY, 5)
    }

    override suspend fun setBrightnessSettings(brightnessSettingUI: BrightnessSettingUI) {
        localStorage.putInt(LocalStorageKeys.BRIGHTNESS_VALUE_KEY, brightnessSettingUI.brightness)
        localStorage.putInt(
            LocalStorageKeys.BRIGHTNESS_GRADUALLY_KEY,
            brightnessSettingUI.brightnessGraduallyMinutes
        )
    }

    override suspend fun getBrightnessSettings(): BrightnessSettingUI {
        val brightness = localStorage.getInt(LocalStorageKeys.BRIGHTNESS_VALUE_KEY, 5)
        val brightnessGradually = localStorage.getInt(LocalStorageKeys.BRIGHTNESS_GRADUALLY_KEY, 0)
        return BrightnessSettingUI(brightness, brightnessGradually)
    }

    override suspend fun getFirstDayOfWeek(): String {
        return localStorage.getString(LocalStorageKeys.FIRST_DAY_OF_WEEK_KEY, weekDays[0].fullName)
    }

    override suspend fun setFirstDayOfWeek(firstDayOfWeek: String) {
        localStorage.putString(LocalStorageKeys.FIRST_DAY_OF_WEEK_KEY, firstDayOfWeek)
    }

    override suspend fun setCurrentSoundAlarmIdForNotification(soundAlarmId: Int) {
        localStorage.putInt(
            LocalStorageKeys.CURRENT_SOUND_ALARM_ID_FOR_NOTIFICATION_KEY,
            soundAlarmId
        )
    }

    override suspend fun getCurrentSoundAlarmIdForNotification(): Int {
        return localStorage.getInt(LocalStorageKeys.CURRENT_SOUND_ALARM_ID_FOR_NOTIFICATION_KEY, -1)
    }

}