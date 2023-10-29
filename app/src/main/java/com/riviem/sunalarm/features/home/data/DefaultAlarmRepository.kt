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
import java.util.Calendar
import javax.inject.Inject


class DefaultAlarmRepository @Inject constructor(
    private val alarmDatabase: AlarmDatabase,
): AlarmRepository {

    override fun getAlarms(): Flow<List<DatabaseAlarm>> {
        return alarmDatabase.alarmDao.getAlarms()
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

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarm.ringTime.hour)
        calendar.set(Calendar.MINUTE, alarm.ringTime.minute)
        calendar.set(Calendar.SECOND, 0)

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("createdTimestamp", alarm.createdTimestamp)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.createdTimestamp, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        println("vladlog: setLightAlarm: ${calendar.time}")

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