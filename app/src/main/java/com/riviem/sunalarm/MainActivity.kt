package com.riviem.sunalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.riviem.sunalarm.features.home.presentation.HomeRoute
import com.riviem.sunalarm.ui.theme.SunAlarmTheme
import dagger.hilt.android.AndroidEntryPoint

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("vladlog: AlarmReceiver Alarm Triggered!")
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_LONG).show()
    }
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SunAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeRoute()
                }
            }
        }
    }
}

