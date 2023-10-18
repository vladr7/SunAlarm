package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
    alarm: AlarmUIModel
) {
    ScrollableTimePicker()
}

@Composable
fun ScrollableTimePicker(
    modifier: Modifier = Modifier,
    selectedHour: Int = 15,
    selectedMinute: Int = 39,
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

    Row(
        modifier = modifier
            .height(240.dp)
            .fillMaxWidth(),
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            state = hoursState
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            state = minutesState
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
            .padding(bottom = 30.dp)
            .wrapContentSize(Alignment.Center)
    )
}