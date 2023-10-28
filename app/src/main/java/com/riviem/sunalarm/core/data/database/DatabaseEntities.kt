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
    val id: String,
    val time: String,
    val name: String,
    val isOn: Boolean,
    val days: List<Day>,
    val color: Int
)

fun AlarmUIModel.asDatabaseModel(): DatabaseAlarm {
    return DatabaseAlarm(
        id = id,
        time = time.toString(),
        name = name,
        isOn = isOn,
        days = days,
        color = color.toArgb()
    )
}

fun DatabaseAlarm.asUIModel(): AlarmUIModel {
    return AlarmUIModel(
        id = id,
        time = ZonedDateTime.parse(time),
        name = name,
        isOn = isOn,
        days = days,
        color = Color(color)
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

