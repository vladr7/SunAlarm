package com.riviem.sunalarm.core.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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


