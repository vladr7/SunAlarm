package com.riviem.sunalarm.core.data.api.sunrise.models

import kotlinx.serialization.Serializable

@Serializable
data class SunriseResults(
    val sunrise: String,
    val sunset: String,
    val first_light: String,
    val last_light: String,
    val dawn: String,
    val dusk: String,
    val solar_noon: String,
    val golden_hour: String,
    val day_length: String,
    val timezone: String,
    val utc_offset: Int
)

