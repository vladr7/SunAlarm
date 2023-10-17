package com.riviem.sunalarm.features.home.presentation

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.riviem.sunalarm.MainActivity
import com.riviem.sunalarm.features.home.presentation.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.models.DayUIModel
import com.riviem.sunalarm.features.home.presentation.models.allDoorsSelected


@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    HomeScreen(
        context = context,
    )
}

@SuppressLint("ScheduleExactAlarm")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
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
                onAlarmClick = {

                },
                onCheckedChange = { checked, alarm ->

                }
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
            isOn = false
        ),
        AlarmUIModel(
            id = 1,
            time = "07:10",
            isOn = true
        ),
        AlarmUIModel(
            id = 2,
            time = "07:20",
            isOn = true
        ),
        AlarmUIModel(
            id = 3,
            time = "07:30",
            isOn = true
        ),
        AlarmUIModel(
            id = 4,
            time = "07:40",
            isOn = false
        ),
        AlarmUIModel(
            id = 5,
            time = "07:50",
            isOn = false
        ),
    )
) {
    LazyColumn(
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
                    isOn = item.isOn
                )
            }
        }
    )
}

@Composable
fun AlarmItem(
    modifier: Modifier = Modifier,
    time: String,
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
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(10.dp)
            )
            .height(140.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        AlarmSelectedDays(
            modifier = modifier,
            days = days
        )
        AlarmSwitch(
            checked = isOn,
            onCheckedChange = onCheckedChange
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
        Row {
            days.forEach { day ->
                DayWithOrWithoutDot(
                    modifier = modifier
                        .padding(start = 10.dp, end = 10.dp),
                    day = day.day,
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
    if (isSelected) {
        Column {
            Icon(
                imageVector = Icons.Filled.Circle, contentDescription = null,
                modifier = modifier
                    .size(5.dp),
                tint = Color.White
            )
            Text(text = day, fontSize = 12.sp, color = Color.White)
        }
    } else {
        Text(text = day, fontSize = 12.sp, color = Color.White)
    }
}

@Composable
fun AlarmSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        })
}







