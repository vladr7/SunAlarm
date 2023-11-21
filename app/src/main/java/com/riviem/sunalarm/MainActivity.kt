package com.riviem.sunalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PowerManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.riviem.sunalarm.core.Constants
import com.riviem.sunalarm.core.Constants.CAMERA_REQUEST_CODE
import com.riviem.sunalarm.core.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.riviem.sunalarm.core.presentation.ACTION_DISMISS_ALARM
import com.riviem.sunalarm.core.presentation.askPermissionDisplayOverOtherApps
import com.riviem.sunalarm.core.presentation.enums.AlarmType
import com.riviem.sunalarm.features.light.LightScreen
import com.riviem.sunalarm.navigation.MainNavigation
import com.riviem.sunalarm.ui.theme.SunAlarmTheme
import dagger.hilt.android.AndroidEntryPoint

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val createdTimestamp = intent?.getIntExtra(Constants.CREATED_TIMESTAMP_ID, -1)
        val alarmType = intent?.getStringExtra(Constants.ALARM_TYPE_ID)

        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivityIntent.putExtra(Constants.FROM_ALARM_ID, true)
        mainActivityIntent.putExtra(Constants.ALARM_TYPE_ID, alarmType)
        mainActivityIntent.putExtra(Constants.CREATED_TIMESTAMP_ID, createdTimestamp)
        context?.startActivity(mainActivityIntent)
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_DISMISS_ALARM) {
            val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                action = ACTION_DISMISS_ALARM
            }
            context.startActivity(mainActivityIntent)
        }
    }
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startedFromAlarm = intent.getBooleanExtra(Constants.FROM_ALARM_ID, false)
        val createdTimestamp = intent.getIntExtra(Constants.CREATED_TIMESTAMP_ID, -1)
        val alarmTypeString = intent.getStringExtra(Constants.ALARM_TYPE_ID)
        val alarmType = if(alarmTypeString == null) AlarmType.LIGHT else AlarmType.valueOf(alarmTypeString)

        if(intent.action == ACTION_DISMISS_ALARM) {
            mainViewModel.cancelSoundAlarm(
                context = this,
            )
            finish()
        } else {
            turnOnScreen()
            setContent {
                SunAlarmTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        askPermissionDisplayOverOtherApps(this)
                        if (startedFromAlarm) {
                            LightScreen(
                                createdTimestamp = createdTimestamp,
                                alarmType = alarmType,
                                wakeLock = wakeLock
                            )
                        } else {
//                        LightScreen(
//                            createdTimestamp = createdTimestamp,
//                            alarmType = alarmType,
//                            wakeLock = wakeLock
//                        )
                            MainNavigation()
                        }
                    }
                }
            }
        }
    }

    private fun turnOnScreen() {
        this.setTurnScreenOn(true)
        this.setShowWhenLocked(true)
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SunAlarm::MyWakeLockTag")
        wakeLock?.acquire(Constants.KEEP_LIGHT_SCREEN_ON_FOR_MINUTES)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted. Do camera operations.
                } else {
                    // Permission was denied. Inform the user or take alternative actions.
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                } else {
                    // Permission was denied. Inform the user or take alternative actions.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseWakeLock(wakeLock)
    }
}

fun releaseWakeLock(wakeLock: PowerManager.WakeLock?) {
    if (wakeLock?.isHeld == true) {
        wakeLock.release()
    }
}
