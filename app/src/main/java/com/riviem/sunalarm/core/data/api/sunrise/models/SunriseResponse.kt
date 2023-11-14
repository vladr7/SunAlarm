package com.riviem.sunalarm.core.data.api.sunrise.models

import kotlinx.serialization.Serializable

@Serializable
data class SunriseResponse(
    val results: SunriseResults,
    val status: String
)