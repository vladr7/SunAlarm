package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import kotlinx.coroutines.delay

@Composable
fun TimePickerScreen(
    alarm: AlarmUIModel,
    onSaveClick: (AlarmUIModel) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ScrollableTimePicker(
            onHourSelected = {
                println("vladlog: onHourSelected: $it")
            },
            onMinuteSelected = {
                println("vladlog: onMinuteSelected: $it")
            },
        )
        Button(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = {
                onSaveClick(alarm)
            }
        ) {
            Text(text = "Save")
        }
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