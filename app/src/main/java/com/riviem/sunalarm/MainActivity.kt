package com.riviem.sunalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.riviem.sunalarm.core.Constants
import com.riviem.sunalarm.features.light.LightScreen
import com.riviem.sunalarm.navigation.MainNavigation
import com.riviem.sunalarm.ui.theme.SunAlarmTheme
import dagger.hilt.android.AndroidEntryPoint

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val createdTimestamp = intent?.getIntExtra(Constants.CREATED_TIMESTAMP_ID, -1)
        println("vladlog: AlarmReceiver Alarm Triggered! createdTimestamp: $createdTimestamp")

        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivityIntent.putExtra(Constants.FROM_ALARM_ID, true)
        mainActivityIntent.putExtra(Constants.CREATED_TIMESTAMP_ID, createdTimestamp)
        context?.startActivity(mainActivityIntent)
    }
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setTurnScreenOn(true)
        this.setShowWhenLocked(true)
        val startedFromAlarm = intent.getBooleanExtra(Constants.FROM_ALARM_ID, false)
        val createdTimestamp = intent.getIntExtra(Constants.CREATED_TIMESTAMP_ID, -1)
        println("vladlog: MainActivity onCreate: createdTimestamp: $createdTimestamp")

        setContent {
            SunAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    askPermissionDisplayOverOtherApps(this)
                    askBrightnessPermission(this)
                    if(startedFromAlarm) {
                        LightScreen(
                            createdTimestamp = createdTimestamp
                        )
                    } else {
//                        LightScreen(
//                            createdTimestamp = createdTimestamp
//                        )
                        MainNavigation()
                    }
                }
            }
        }
    }
}

private fun askBrightnessPermission(mainActivity: MainActivity) {
    if (!Settings.System.canWrite(mainActivity)) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + mainActivity.packageName)
        mainActivity.startActivity(intent)
    }

}

private fun askPermissionDisplayOverOtherApps(
    context: Context
) {
    if (Settings.canDrawOverlays(context)) {
        // You have the permission
    } else {
        // You do not have the permission. Open the settings to let user grant it.
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
        context.startActivity(intent)
    }

}
