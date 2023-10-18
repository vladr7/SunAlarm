package com.riviem.sunalarm.features.home.presentation.homescreen

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.sunalarm.MainActivity
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.DayUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.allDoorsSelected
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.TimePickerScreen


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
        )
    } else {
        TimePickerScreen(
            alarm = state.selectedAlarm ?: AlarmUIModel(
                id = 0, time = "07:00", name = "Alarm 1", isOn = false, days = listOf()
            ),
            onSaveClick = { alarm ->
                viewModel.onSaveAlarmClick(alarm)
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
            HomeScreenTitle(
                modifier = modifier
                    .padding(top = 100.dp),
            )
            AddNewAlarmButton(
                modifier = modifier
                    .padding(top = 20.dp),
                onClick = {

                }
            )
            AlarmsList(
                onAlarmClick = onAlarmClick,
                onCheckedChange = { checked, alarm ->

                },
                modifier = modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun HomeScreenTitle(
    modifier: Modifier = Modifier,
    title: String = "Next alarm in 6 hours 1 minute",
    subtitle: String = "Tue, Oct 17, 15:29",
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 30.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
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
    alarms: List<AlarmUIModel> = listOf(
        AlarmUIModel(
            id = 0,
            time = "07:00",
            isOn = false,
            name = "Alarm 1",
        ),
        AlarmUIModel(
            id = 1,
            time = "07:10",
            isOn = true,
            name = "Alarm 1",
        ),
        AlarmUIModel(
            id = 2,
            time = "07:20",
            isOn = true,
            name = "Alarm 1",

            ),
        AlarmUIModel(
            id = 3,
            time = "07:30",
            isOn = true,
            name = "Alarm 1",

            ),
        AlarmUIModel(
            id = 4,
            time = "07:40",
            isOn = false,
            name = "Alarm 1",

            ),
        AlarmUIModel(
            id = 5,
            time = "07:50",
            isOn = false,
            name = "Alarm 1",
        ),
    )
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
                    time = item.time,
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
    time: String,
    name: String,
    isOn: Boolean,
    onAlarmClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    days: List<DayUIModel>
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
private fun AlarmNameAndTime(name: String, time: String) {
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
            text = time,
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 18.dp, top = 10.dp)
        )
    }
}

@Composable
fun AlarmSelectedDays(
    modifier: Modifier = Modifier,
    days: List<DayUIModel>
) {
    if (days.allDoorsSelected()) {
        Text(text = "Every day", fontSize = 16.sp, color = Color.White)
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
                    day = day.day.dayLetter,
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







