package com.riviem.sunalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.riviem.sunalarm.features.light.LightScreen
import com.riviem.sunalarm.navigation.MainNavigation
import com.riviem.sunalarm.ui.theme.SunAlarmTheme
import dagger.hilt.android.AndroidEntryPoint

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("vladlog: AlarmReceiver Alarm Triggered!")
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_LONG).show()

        val mainActivityIntent = Intent(context, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivityIntent.putExtra("fromAlarm", true)  // Add this line
        context?.startActivity(mainActivityIntent)
    }
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setTurnScreenOn(true)
        this.setShowWhenLocked(true)
        val startedFromAlarm = intent.getBooleanExtra("fromAlarm", false)

        setContent {
            SunAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    askPermissionDisplayOverOtherApps(this)
                    if(startedFromAlarm) {
                        LightScreen()
                    } else {
                        MainNavigation()
                    }
                }
            }
        }
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
