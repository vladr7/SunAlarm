package com.riviem.sunalarm.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.riviem.sunalarm.features.home.data.models.Alarm
import com.riviem.sunalarm.features.home.presentation.homescreen.models.DayUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.weekDays

@Entity
data class DatabaseAlarm (
    @PrimaryKey
    val id: String = "",
    val time: String,
    val name: String,
    val isOn: Boolean,
    val days: List<DayUIModel> = weekDays
)

fun List<DatabaseAlarm>.asDomainModel(): List<Alarm> {
    return map {
        Alarm(

        )
    }
}

fun List<Food>.asDatabaseModel(): List<DatabaseAlarm> {
    return this.map {
        DatabaseAlarm(
            id = it.id,
            author = it.author,
            authorUid = it.authorUid,
            description = it.description,
            addedDateInSeconds = it.addedDateInSeconds,
            imageRef = it.imageRef,
            category = it.category,
            ingredients = it.ingredients,
            title = it.title
        )
    }
}

