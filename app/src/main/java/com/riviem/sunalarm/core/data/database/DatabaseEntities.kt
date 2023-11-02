package com.riviem.sunalarm.core.data.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import java.time.ZonedDateTime

@Entity
data class DatabaseAlarm(
    @PrimaryKey
    val createdTimestamp: Int,
    val ringTime: String,
    val name: String,
    val isOn: Boolean,
    val days: List<Day>,
    val color: Int,
    val flashlight: Boolean,
    val soundEnabled: Boolean,
    val minutesUntilSound: Int,
)

fun AlarmUIModel.asDatabaseModel(): DatabaseAlarm {
    return DatabaseAlarm(
        createdTimestamp = createdTimestamp,
        ringTime = ringTime.toString(),
        name = name,
        isOn = isOn,
        days = days,
        color = color.toArgb(),
        flashlight = flashlight,
        soundEnabled = soundAlarmEnabled,
        minutesUntilSound = minutesUntilSoundAlarm,
    )
}

fun DatabaseAlarm.asUIModel(): AlarmUIModel {
    return AlarmUIModel(
        createdTimestamp = createdTimestamp,
        ringTime = ZonedDateTime.parse(ringTime),
        name = name,
        isOn = isOn,
        days = days,
        color = Color(color),
        flashlight = flashlight,
        soundAlarmEnabled = soundEnabled,
        minutesUntilSoundAlarm = minutesUntilSound,
    )
}

fun List<DatabaseAlarm>.asUIModel(): List<AlarmUIModel> {
    return this.map {
        it.asUIModel()
    }
}

fun List<AlarmUIModel>.asDatabaseModel(): List<DatabaseAlarm> {
    return this.map {
        it.asDatabaseModel()
    }
}

