package com.riviem.sunalarm.features.home.presentation.homescreen.models

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Day(
    @DocumentId
    val letter: String,
    val fullName: String,
    val isSelected: Boolean
): java.io.Serializable

fun List<Day>.allDoorsSelected(): Boolean {
    return this.all { it.isSelected }
}


