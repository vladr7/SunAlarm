package com.riviem.sunalarm.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import java.time.ZonedDateTime

@Entity
data class DatabaseAlarm(
    @PrimaryKey
    val id: String,
    val time: String,
    val name: String,
    val isOn: Boolean,
    val days: List<Day>
)

fun AlarmUIModel.asDatabaseModel(): DatabaseAlarm {
    return DatabaseAlarm(
        id = id,
        time = time.toString(),
        name = name,
        isOn = isOn,
        days = days
    )
}

fun DatabaseAlarm.asUIModel(): AlarmUIModel {
    return AlarmUIModel(
        id = id,
        time = ZonedDateTime.parse(time),
        name = name,
        isOn = isOn,
        days = days
    )
}

fun List<DatabaseAlarm>.asUIModel(): List<AlarmUIModel> {
    return map {
        AlarmUIModel(
            id = it.id,
            time = ZonedDateTime.parse(it.time),
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
            time = it.time.toString(),
            name = it.name,
            isOn = it.isOn,
            days = it.days
        )
    }
}

