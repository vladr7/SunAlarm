package com.riviem.sunalarm.features.home.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.riviem.sunalarm.AlarmReceiver
import com.riviem.sunalarm.core.data.database.AlarmDatabase
import com.riviem.sunalarm.core.data.database.DatabaseAlarm
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import javax.inject.Inject


class DefaultAlarmRepository @Inject constructor(
    private val alarmDatabase: AlarmDatabase,
): AlarmRepository {

    override fun getAlarms(): Flow<List<DatabaseAlarm>> {
        return alarmDatabase.alarmDao.getAlarms()
    }

    override fun getAlarmById(createdTimestampId: Int): DatabaseAlarm {
        return alarmDatabase.alarmDao.getAlarmById(createdTimestampId)
    }

    override fun insertAll(alarms: List<DatabaseAlarm>) {
        alarmDatabase.alarmDao.insertAll(alarms)
    }

    override fun deleteAllAlarms() {
        alarmDatabase.alarmDao.deleteAllAlarms()
    }

    override fun insert(alarm: DatabaseAlarm) {
        alarmDatabase.alarmDao.insert(alarm)
    }

    override fun setLightAlarm(alarm: AlarmUIModel, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val currentDayOfWeek = now.dayOfWeek.value % 7 // 0 - Monday, 1 - Tuesday, etc.

        val currentHour = now.hour
        val currentMinute = now.minute
        val currentTimeInMinutes = currentHour * 60 + currentMinute
        val alarmTimeInMinutes = alarm.ringTime.hour * 60 + alarm.ringTime.minute
        val alarmTime = alarm.ringTime

        val daysToNextAlarm = if (alarm.days[currentDayOfWeek].isSelected && currentTimeInMinutes <= alarmTimeInMinutes - 1) {
            println("vladlog: setLightAlarm for today")
            0L // Set alarm for today
        } else {
            // Find the next selected day after today
            println("vladlog: setLightAlarm for next day")
            val daysAfterCurrent = (alarm.days.drop(currentDayOfWeek + 1) + alarm.days.take(currentDayOfWeek + 1))
            daysAfterCurrent.indexOfFirst { it.isSelected } + 1L
        }

        val alarmDateTime = now
            .withHour(alarmTime.hour)
            .withMinute(alarmTime.minute)
            .withSecond(0)
            .plusDays(daysToNextAlarm)

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("createdTimestamp", alarm.createdTimestamp)

        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.createdTimestamp, intent, PendingIntent.FLAG_IMMUTABLE
        )

        println("vladlog: setLightAlarm for: $alarmDateTime")

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmDateTime.toInstant().toEpochMilli(), pendingIntent)
    }

    override fun snoozeAlarm(alarm: AlarmUIModel, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("createdTimestamp", alarm.createdTimestamp)

        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.createdTimestamp, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 5)

        println("vladlog: snoozeAlarm: ${calendar.time}")

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    override fun cancelAlarm(alarm: AlarmUIModel, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.createdTimestamp, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        println("vladlog: cancelAlarm: ${alarm.ringTime}")
        alarmManager.cancel(pendingIntent)
    }
}