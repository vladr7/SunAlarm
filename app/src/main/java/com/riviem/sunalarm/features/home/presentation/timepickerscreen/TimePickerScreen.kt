package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.riviem.sunalarm.R
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import kotlinx.coroutines.delay

@Composable
fun TimePickerScreen(
    alarm: AlarmUIModel,
    onSaveClick: (AlarmUIModel) -> Unit,
    onCancelClick: () -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var newAlarm by remember { mutableStateOf(alarm) }
    var newColor by remember { mutableStateOf(alarm.color) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ScrollableTimePicker(
                onHourSelected = {
                    newAlarm = newAlarm.copy(
                        ringTime = newAlarm.ringTime.withHour(it)
                    )
                },
                onMinuteSelected = {
                    newAlarm = newAlarm.copy(
                        ringTime = newAlarm.ringTime.withMinute(it)
                    )
                },
                modifier = Modifier
                    .height(300.dp),
                selectedHour = alarm.ringTime.hour,
                selectedMinute = alarm.ringTime.minute
            )
            LightAlarmConfiguration(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .weight(1f)
                    .fillMaxHeight(),
                alarm = newAlarm,
                onDayClicked = {
                    val numberOfSelectedDays = newAlarm.days.filter { day ->
                        day.isSelected
                    }.size
                    newAlarm = newAlarm.copy(
                        days = newAlarm.days.map { day ->
                            if (day.fullName == it.fullName && (numberOfSelectedDays > 1 || !day.isSelected)) {
                                day.copy(isSelected = !day.isSelected)
                            } else {
                                day
                            }
                        }
                    )
                },
                onChooseColorClicked = {
                    showColorPicker = !showColorPicker
                },
                onAlarmNameChange = {
                    newAlarm = newAlarm.copy(
                        name = it
                    )
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CancelButton(
                    modifier = Modifier,
                    onCancelClick = {
                        onCancelClick()
                    }
                )
                SaveButton(
                    modifier = Modifier,
                    onSaveClick = {
                        onSaveClick(
                            newAlarm.copy(
                                isOn = true
                            )
                        )
                    },
                )
            }
        }

        AnimatedVisibility(
            visible = showColorPicker,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ColorPickerDialog(
                onColorChanged = {
                    newColor = it
                },
                onSaveColorClicked = {
                    newAlarm = newAlarm.copy(
                        color = newColor
                    )
                    showColorPicker = false
                },
                onCancelColorClicked = {
                    showColorPicker = false
                },
            )
        }
    }
}

@Composable
fun CancelButton(
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .padding(16.dp),
        onClick = {
            onCancelClick()
        }
    ) {
        Text(text = stringResource(id = R.string.cancel))
    }
}

@Composable
fun ColorPickerDialog(
    modifier: Modifier = Modifier,
    onColorChanged: (Color) -> Unit,
    onSaveColorClicked: () -> Unit,
    onCancelColorClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                shape = MaterialTheme.shapes.large,
                color = Color.Black.copy(alpha = 0.9f)
            )
    ) {
        ColorPicker(
            onColorChanged = onColorChanged,
            modifier = Modifier
                .align(Alignment.Center)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 100.dp
                )
        ) {
            CancelColorButton(
                modifier = Modifier
                    .padding(bottom = 20.dp, start = 20.dp),
                onCancelClick = onCancelColorClicked
            )
            SaveColorButton(
                modifier = Modifier
                    .padding(bottom = 20.dp, end = 20.dp),
                onSaveClick = onSaveColorClicked
            )
        }
    }
}

@Composable
fun CancelColorButton(
    modifier: Modifier,
    onCancelClick: () -> Unit
) {
    Button(
        onClick = onCancelClick,
        modifier = modifier
            .width(150.dp)
            .height(50.dp)
    ) {
        Text(
            text = stringResource(R.string.cancel),
            fontSize = 18.sp,
            color = Color.White
        )
    }
}

@Composable
fun SaveColorButton(
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit
) {
    Button(
        onClick = onSaveClick,
        modifier = modifier
            .width(150.dp)
            .height(50.dp)
    ) {
        Text(
            text = stringResource(R.string.save_color),
            fontSize = 18.sp,
            color = Color.White
        )
    }
}

@Composable
fun LightAlarmConfiguration(
    modifier: Modifier = Modifier,
    alarm: AlarmUIModel,
    onDayClicked: (Day) -> Unit,
    onChooseColorClicked: () -> Unit,
    onAlarmNameChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
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
        ChangeAlarmName(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 15.dp, end = 15.dp),
            alarmName = alarm.name,
            onAlarmNameChange = {
                onAlarmNameChange(it)
            }
        )
        ChooseScreenColor(
            modifier = Modifier
                .padding(top = 25.dp, start = 25.dp, end = 15.dp),
            onChooseColorClicked = onChooseColorClicked,
            color = alarm.color
        )
    }
}

@Composable
fun ChooseScreenColor(
    modifier: Modifier = Modifier,
    onChooseColorClicked: () -> Unit,
    color: Color
) {
    Row(
        modifier = modifier
            .clickable {
                onChooseColorClicked()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.screen_color),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
        )
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .size(25.dp)
                .background(
                    shape = CircleShape,
                    color = color
                )
        )
    }
}

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    onColorChanged: (Color) -> Unit,
) {
    val controller = rememberColorPickerController()

    HsvColorPicker(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(10.dp),
        controller = controller,
        onColorChanged = { colorEnvelope: ColorEnvelope ->
            val color: Color = colorEnvelope.color
            val hexCode: String = colorEnvelope.hexCode
            val fromUser: Boolean = colorEnvelope.fromUser
            onColorChanged(color)
        }
    )
}

@Composable
fun ChangeAlarmName(
    modifier: Modifier = Modifier,
    alarmName: String,
    onAlarmNameChange: (String) -> Unit
) {
    TextField(
        value = alarmName,
        onValueChange = onAlarmNameChange,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        ),
    )
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
                day = dayUIModel,
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

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSelected) 1.5.dp else 0.dp, label = ""
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.3f, label = ""
    )

    val newModifier = modifier
        .size(circleSize)
        .clickable {
            onDayClicked(day)
        }
        .border(
            width = animatedBorderWidth,
            color = Color.White.copy(alpha = animatedAlpha),
            shape = CircleShape
        )

    Box(
        modifier = newModifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.letter,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}


@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .padding(16.dp),
        onClick = {
            onSaveClick()
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
    selectedHour: Int,
    selectedMinute: Int,
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

    val localDensity = LocalDensity.current

    LaunchedEffect(
        key1 = hoursState.firstVisibleItemIndex,
        hoursState.firstVisibleItemScrollOffset
    ) {
        val index = hoursState.firstVisibleItemIndex
        val offset = hoursState.firstVisibleItemScrollOffset
        val actualIndex =
            if (offset > with(localDensity) { 50.dp.toPx() }) index + 1 else index
        val hour = hours[actualIndex]
        onHourSelected((hour.toInt() + 1) % 24)
    }

    LaunchedEffect(
        key1 = minutesState.firstVisibleItemIndex,
        minutesState.firstVisibleItemScrollOffset
    ) {
        val index = minutesState.firstVisibleItemIndex
        val offset = minutesState.firstVisibleItemScrollOffset
        val actualIndex = if (offset > with(localDensity) { 50.dp.toPx() }) index + 1 else index
        val minute = minutes[actualIndex]
        onMinuteSelected((minute.toInt() + 1) % 60)
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
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
                modifier = Modifier.padding(start = 5.dp, end = 5.dp)
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
        TransparentRectangle(
            modifier = Modifier.align(Alignment.TopCenter)
        )
        TransparentRectangle(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TransparentRectangle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
    )
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