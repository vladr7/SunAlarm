package com.riviem.sunalarm.features.home.data

import android.content.Context
import com.riviem.sunalarm.core.data.database.DatabaseAlarm
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun getAlarms(): Flow<List<DatabaseAlarm>>

    fun getAlarmById(createdTimestampId: Int): DatabaseAlarm

    fun insertAll(alarms: List<DatabaseAlarm>)

    fun deleteAllAlarms()

    fun insert(alarm: DatabaseAlarm)

    fun setLightAlarm(alarm: AlarmUIModel, context: Context)

    fun snoozeAlarm(alarm: AlarmUIModel, context: Context)

    fun cancelAlarm(alarm: AlarmUIModel, context: Context)
}