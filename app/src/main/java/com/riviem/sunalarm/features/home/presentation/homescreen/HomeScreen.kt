package com.riviem.sunalarm.features.home.presentation.homescreen

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.sunalarm.AlarmReceiver
import com.riviem.sunalarm.MainActivity
import com.riviem.sunalarm.R
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import com.riviem.sunalarm.features.home.presentation.homescreen.models.allDoorsSelected
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.TimePickerScreen
import kotlinx.coroutines.delay
import java.time.ZonedDateTime
import java.util.Calendar


@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onAlarmClick: () -> Unit,
    onSaveOrDiscardClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (!state.showTimePickerScreen) {
        HomeScreen(
            context = context,
            onAlarmClick = { alarm ->
                viewModel.onAlarmClick(alarm)
                onAlarmClick()
            },
            alarms = state.alarms,
            onAddNewAlarmClick = {
                viewModel.onAddNewAlarmClick()
            },
            onAlarmCheckChanged = { checked, alarm ->
                viewModel.onAlarmCheckChanged(checked, alarm, context)
            },
            title = state.title,
            subtitle = state.subtitle,
        )
    }

    AnimatedVisibility(
        visible = state.showTimePickerScreen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        TimePickerScreen(
            alarm = state.selectedAlarm,
            onSaveClick = { alarm ->
                viewModel.onSaveAlarmClick(alarm, context)
                onSaveOrDiscardClick()
            },
            onCancelClick = {
                viewModel.onCancelAlarmClick()
                onSaveOrDiscardClick()
            }
        )
    }
}

@SuppressLint("ScheduleExactAlarm")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onAlarmClick: (AlarmUIModel) -> Unit,
    alarms: List<AlarmUIModel>,
    onAddNewAlarmClick: () -> Unit,
    onAlarmCheckChanged: (Boolean, AlarmUIModel) -> Unit,
    title: String,
    subtitle: String,
) {
    val activity = context as MainActivity

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = modifier
                    .padding(top = 50.dp)
                    .weight(0.30f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                HomeScreenTitle(
                    modifier = modifier.fillMaxWidth(),
                    title = title,
                    subtitle = subtitle
                )
            }
            Column(
                modifier = modifier.weight(0.70f)
            ) {
                AddNewAlarmButton(
                    onClick = {
                        onAddNewAlarmClick()
                    }
                )

                AlarmsList(
                    onAlarmClick = onAlarmClick,
                    onCheckedChange = { checked, alarm ->
                        onAlarmCheckChanged(checked, alarm)
                    },
                    modifier = modifier.fillMaxWidth(),
                    alarms = alarms
                )
            }
        }
    }
}

@Composable
fun HomeScreenTitle(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
) {
    var showContent by remember {
        mutableStateOf(false)
    }
    var newTitle by remember {
        mutableStateOf(title)
    }
    var newSubtitle by remember {
        mutableStateOf(subtitle)
    }
    LaunchedEffect(key1 = title) {
        showContent = false
        delay(500L)
        newTitle = title
        newSubtitle = subtitle
        showContent = true
    }

    Column(
        modifier = modifier.padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = newTitle,
                fontSize = 30.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize(),
                lineHeight = 36.sp
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        AnimatedVisibility(
            visible = showContent,
        ) {
            Text(
                text = newSubtitle,
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize()
            )
        }
    }
}


@Composable
fun AddNewAlarmButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.weight(0.8f))
        Icon(
            imageVector = Icons.Filled.Add, contentDescription = "Add new alarm",
            modifier = modifier
                .weight(0.2f)
                .clickable {
                    onClick()
                }
                .padding(start = 10.dp, end = 24.dp)
                .size(70.dp),
            tint = Color.White
        )
    }
}

@Composable
fun AlarmsList(
    modifier: Modifier = Modifier,
    onAlarmClick: (AlarmUIModel) -> Unit,
    onCheckedChange: (Boolean, AlarmUIModel) -> Unit,
    alarms: List<AlarmUIModel>
) {
    LazyColumn(
        modifier = modifier,
        content = {
            items(alarms) { item ->
                AlarmItem(
                    onAlarmClick = {
                        onAlarmClick(item)
                    },
                    onCheckedChange = {
                        onCheckedChange(it, item)
                    },
                    days = item.days,
                    time = item.ringTime,
                    isOn = item.isOn,
                    name = item.name
                )
            }
        }
    )
}

@Composable
fun AlarmItem(
    modifier: Modifier = Modifier,
    time: ZonedDateTime,
    name: String,
    isOn: Boolean,
    onAlarmClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    days: List<Day>
) {
    Row(
        modifier = modifier
            .clickable {
                onAlarmClick()
            }
            .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(10.dp)
            )
            .height(120.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlarmNameAndTime(name, time)
        Spacer(modifier = Modifier.weight(1f))
        AlarmSelectedDays(
            modifier = modifier,
            days = days
        )
        AlarmSwitch(
            modifier = modifier
                .padding(end = 10.dp),
            checked = isOn,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun AlarmNameAndTime(name: String, time: ZonedDateTime) {
    val hour = if (time.hour < 10) "0${time.hour}" else time.hour
    val minute = if (time.minute < 10) "0${time.minute}" else time.minute
    Column {
        Text(
            text = name,
            fontSize = 17.sp,
            color = Color.White,
            modifier = Modifier
                .padding(start = 15.dp)
                .offset(y = (-10).dp)
        )
        Text(
            text = "$hour:$minute",
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 18.dp, top = 10.dp)
        )
    }
}

@Composable
fun AlarmSelectedDays(
    modifier: Modifier = Modifier,
    days: List<Day>
) {
    if (days.allDoorsSelected()) {
        Text(
            text = stringResource(R.string.every_day), fontSize = 16.sp, color = Color.White,
            modifier = modifier
                .padding(end = 10.dp)
        )
    } else {
        Row(
            modifier = modifier
                .padding(end = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { day ->
                DayWithOrWithoutDot(
                    modifier = modifier
                        .padding(end = 3.dp),
                    day = day.letter,
                    isSelected = day.isSelected
                )
            }
        }
    }
}

@Composable
fun DayWithOrWithoutDot(
    modifier: Modifier = Modifier,
    day: String,
    isSelected: Boolean,
) {
    Column(
        modifier = modifier,
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Circle, contentDescription = null,
                modifier = modifier
                    .size(6.dp),
                tint = Color.White
            )
        }
        Text(
            text = day, fontSize = 14.sp, color = Color.White,
            modifier = modifier
                .padding(top = 2.dp)
                .alpha(if (isSelected) 1f else 0.5f)
        )
    }
}

@Composable
fun AlarmSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        })
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





