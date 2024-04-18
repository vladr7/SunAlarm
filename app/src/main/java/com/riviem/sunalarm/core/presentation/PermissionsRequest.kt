package com.riviem.sunalarm.core.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.riviem.sunalarm.MainActivity

fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

fun askBrightnessPermission(mainActivity: MainActivity) {
    if (!Settings.System.canWrite(mainActivity)) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + mainActivity.packageName)
        mainActivity.startActivity(intent)
    }
}

fun hasBrightnessPermission(context: Context): Boolean {
    return Settings.System.canWrite(context)
}

fun askPermissionDisplayOverOtherApps(
    context: Context
) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )
    context.startActivity(intent)
}


fun hasPermissionDisplayOverOtherApps(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun checkLocationIsEnabled(context: Context): Boolean {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
}

fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                true
            }

            else -> {
                false
            }
        }
    } else {
        true
    }
}

fun navigateToSettings(context: Context, activity: Activity) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        val uri = Uri.fromParts("package", activity.packageName, null)
        data = uri
    }
    context.startActivity(intent)
}

fun isBatteryOptimizationDisabled(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}

fun openAppInfoSettings(context: Context) {
    val uri = Uri.fromParts("package", context.packageName, null)
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = uri
    }
    context.startActivity(intent)
}

