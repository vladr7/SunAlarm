package com.riviem.sunalarm.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day

@Entity
data class DatabaseAlarm(
    @PrimaryKey
    val id: Int,
    val time: String,
    val name: String,
    val isOn: Boolean,
    val days: List<Day>
)

fun List<DatabaseAlarm>.asUIModel(): List<AlarmUIModel> {
    return map {
        AlarmUIModel(
            id = it.id,
            time = it.time,
            name = it.name,
            isOn = it.isOn,
            days = it.days
        )
    }
}

fun List<AlarmUIModel>.asDatabaseModel(): List<DatabaseAlarm> {
    return this.map {
        DatabaseAlarm(
            id = it.id,
            time = it.time,
            name = it.name,
            isOn = it.isOn,
            days = it.days
        )
    }
}

