package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel

@Composable
fun TimePickerScreen(
    alarm: AlarmUIModel
) {
    ScrollableTimePicker()
}

@Composable
fun ScrollableTimePicker(
    modifier: Modifier = Modifier,
) {
    val hours = (1..24).map { if (it < 10) "0$it" else it.toString() }
    val minutes = (0..59).map { if (it < 10) "0$it" else it.toString() }

    Row(
        modifier = modifier
            .height(300.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyColumn(content = {
            items(hours) { hour ->
                Text(
                    text = hour,
                    color = Color.White,
                    fontSize = 50.sp,
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                )
            }
        })
        Text(text = ":", color = Color.White, fontSize = 50.sp)
        LazyColumn(content = {
            items(minutes) { minute ->
                Text(
                    text = minute,
                    color = Color.White,
                    fontSize = 50.sp,
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                )
            }
        })
    }
}


