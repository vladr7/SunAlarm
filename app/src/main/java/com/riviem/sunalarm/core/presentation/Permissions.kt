package com.riviem.sunalarm.core.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.riviem.sunalarm.MainActivity

fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

const val CAMERA_REQUEST_CODE = 101

fun requestCameraPermission(activity: MainActivity) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE
    )
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
    if (Settings.canDrawOverlays(context)) {
        // You have the permission
    } else {
        // You do not have the permission. Open the settings to let user grant it.
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
    }

}
