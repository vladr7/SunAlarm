package com.riviem.sunalarm.core.data.database.typeconverters

import androidx.room.TypeConverter
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DayTypeConverter {

    @TypeConverter
    fun fromDaysList(list: List<Day>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toDaysList(jsonString: String): List<Day> {
        return Json.decodeFromString(jsonString)
    }
}
