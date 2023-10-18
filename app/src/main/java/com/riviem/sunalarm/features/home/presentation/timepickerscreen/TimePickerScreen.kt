package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import kotlinx.coroutines.delay

@Composable
fun TimePickerScreen(
    alarm: AlarmUIModel,
    onSaveClick: (AlarmUIModel) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTimePicker(
            onHourSelected = {
                println("vladlog: onHourSelected: $it")
            },
            onMinuteSelected = {
                println("vladlog: onMinuteSelected: $it")
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        LightAlarmConfiguration(
            modifier = Modifier
                .padding(top = 32.dp)
                .weight(1f)
                .fillMaxHeight(),
            alarm = alarm,
            onDayClicked = {
                println("vladlog: onDayClicked: $it")
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            SaveButton(
                modifier = Modifier,
                onSaveClick = onSaveClick,
                alarm = alarm
            )
        }
    }
}

@Composable
fun LightAlarmConfiguration(
    modifier: Modifier = Modifier,
    alarm: AlarmUIModel,
    onDayClicked: (Day) -> Unit
) {
    Column(
        modifier = modifier
            .background(
                color = Color.DarkGray.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.extraLarge
            )
            .fillMaxWidth()
    ) {
        Text(
            text = alarm.name,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier
                .padding(start = 32.dp, top = 25.dp)
        )
        SelectDays(
            alarm = alarm,
            onDayClicked = {
                onDayClicked(it)
            }
        )

    }
}

@Composable
fun SelectDays(
    modifier: Modifier = Modifier,
    alarm: AlarmUIModel,
    onDayClicked: (Day) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        alarm.days.forEach { dayUIModel ->
            CheckboxDay(
                day = dayUIModel.day,
                isSelected = dayUIModel.isSelected,
                onDayClicked = onDayClicked
            )
        }
    }
}

@Composable
fun CheckboxDay(
    modifier: Modifier = Modifier,
    day: Day,
    isSelected: Boolean,
    onDayClicked: (Day) -> Unit
) {
    val circleSize = 35.dp
    val newModifier = if (isSelected) {
        modifier
            .size(circleSize)
            .clickable {
                onDayClicked(day)
            }
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.7f),
                shape = CircleShape
            )
    } else {
        modifier
            .size(circleSize)
            .clickable {
                onDayClicked(day)
            }
    }

    Box(
        modifier = newModifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayLetter,
            fontSize = 14.sp,
            color = if (isSelected) Color.Blue else Color.White
        )
    }
}


@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    onSaveClick: (AlarmUIModel) -> Unit,
    alarm: AlarmUIModel
) {
    Button(
        modifier = modifier
            .padding(16.dp),
        onClick = {
            onSaveClick(alarm)
        }
    ) {
        Text(text = "Save")
    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableTimePicker(
    modifier: Modifier = Modifier,
    selectedHour: Int = 15,
    selectedMinute: Int = 39,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
) {
    val hoursState = rememberLazyListState()
    val minutesState = rememberLazyListState()

    val hours = infiniteHours.take(480).map { if (it < 10) "0$it" else it.toString() }.toList()
    val minutes = infiniteMinutes.take(1200).map { if (it < 10) "0$it" else it.toString() }.toList()

    LaunchedEffect(key1 = Unit) {
        hoursState.scrollToItem(index = 240 + selectedHour)
        minutesState.scrollToItem(index = 600 + selectedMinute)
        delay(50L)
        hoursState.animateScrollToItem(index = 240 + selectedHour - 2)
        minutesState.animateScrollToItem(index = 600 + selectedMinute - 1)
    }

    LaunchedEffect(key1 = hoursState.firstVisibleItemIndex) {
        val index = hoursState.firstVisibleItemIndex
        val hour = hours[index]
        onHourSelected((hour.toInt() + 1) % 24)
    }

    LaunchedEffect(key1 = minutesState.firstVisibleItemIndex) {
        val index = minutesState.firstVisibleItemIndex
        val minute = minutes[index]
        onMinuteSelected((minute.toInt() + 1) % 60)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                content = {
                    items(hours) { hour ->
                        TimeScrollItem(
                            time = hour,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                },
                state = hoursState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = hoursState)
            )
            Text(
                text = ":",
                color = Color.White,
                fontSize = 50.sp,
                modifier = Modifier.padding(5.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                content = {
                    items(minutes) { minute ->
                        TimeScrollItem(
                            time = minute,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),

                            )
                    }
                },
                state = minutesState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = minutesState)
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(100.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(100.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
        )
    }
}

@Composable
private fun TimeScrollItem(
    modifier: Modifier = Modifier,
    time: String
) {
    Text(
        text = time,
        color = Color.White,
        fontSize = 50.sp,
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    )
}