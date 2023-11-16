package com.riviem.sunalarm.core.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import java.time.LocalTime
import java.time.format.DateTimeFormatter

suspend fun getCoordinates(activity: Activity): Coordinates? {
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return null
    }
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    return try {
        val location = fusedLocationClient.lastLocation.await()
        location?.let {
            Coordinates(
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    } catch (e: Exception) {
        null
    }
}

fun extractHourAndMinute(timeString: String): Pair<Int, Int> {
    val formatter = DateTimeFormatter.ofPattern("h:mm:ss a")
    val time = LocalTime.parse(timeString, formatter)
    return Pair(time.hour, time.minute)
}

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)