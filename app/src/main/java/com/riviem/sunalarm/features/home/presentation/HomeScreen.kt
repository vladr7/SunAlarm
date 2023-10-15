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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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

@SuppressLint("ScheduleExactAlarm")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context
) {

    var hour by remember { mutableIntStateOf(16) }
    var minute by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SetAlarmButton(hour, minute, context)
            Spacer(modifier = Modifier.padding(16.dp))
            TextField(
                value = hour.toString(),
                onValueChange = { hour = if (it.isNotEmpty()) it.toInt() else 0 },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .padding(16.dp)
            )
            TextField(
                value = minute.toString(),
                onValueChange = { minute = if (it.isNotEmpty()) it.toInt() else 0 },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .padding(16.dp)
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
        calendar.set(Calendar.HOUR_OF_DAY, 11)
        calendar.set(Calendar.MINUTE, 21)
        calendar.set(Calendar.SECOND, 0)

        // Create a PendingIntent object for the alarm receiver
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        println("vladlog: Alarm set!")
        Toast.makeText(context, "Alarm set!", Toast.LENGTH_SHORT).show()
    }) {
        Text("Set Alarm")
    }

}
