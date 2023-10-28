package com.riviem.sunalarm.features.home.data

import com.riviem.sunalarm.core.data.database.DatabaseAlarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun getAlarms(): Flow<List<DatabaseAlarm>>

    fun insertAll(alarms: List<DatabaseAlarm>)

    fun deleteAllAlarms()

    fun insert(alarm: DatabaseAlarm)
}