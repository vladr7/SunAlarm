package com.riviem.sunalarm.features.home.presentation

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.riviem.sunalarm.AlarmReceiver
import java.util.Calendar


@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    HomeScreen(
        context = context
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ScheduleExactAlarm")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context
) {
    val timePickerState = remember {
        TimePickerState(
            initialHour = 16,
            initialMinute = 10,
            is24Hour = true
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(16.dp))
            SetAlarmButton(hour = timePickerState.hour, minute = timePickerState.minute, context)
            Spacer(modifier = Modifier.padding(16.dp))
            TimeInput(
                state = timePickerState
            )
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
@Composable
private fun SetAlarmButton(hour: Int, minute: Int, context: Context) {
    Button(onClick = {
        // Get the AlarmManager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create a Calendar object for the alarm time
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        // Create a PendingIntent object for the alarm receiver
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        println("vladlog: Alarm set! $hour:$minute")
        Toast.makeText(context, "Alarm set!", Toast.LENGTH_SHORT).show()
    }) {
        Text("Set Alarm")
    }

}
