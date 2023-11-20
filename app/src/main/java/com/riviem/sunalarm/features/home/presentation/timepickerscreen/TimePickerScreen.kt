package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.riviem.sunalarm.MainActivity
import com.riviem.sunalarm.R
import com.riviem.sunalarm.core.Constants
import com.riviem.sunalarm.core.data.api.sunrise.RetrofitInstance
import com.riviem.sunalarm.core.presentation.ButtonCustom
import com.riviem.sunalarm.core.presentation.SwitchCustom
import com.riviem.sunalarm.core.presentation.checkAndRequestLocationPermission
import com.riviem.sunalarm.core.presentation.checkLocationIsEnabled
import com.riviem.sunalarm.core.presentation.extractHourAndMinute
import com.riviem.sunalarm.core.presentation.getCoordinates
import com.riviem.sunalarm.core.presentation.hasCameraPermission
import com.riviem.sunalarm.core.presentation.hasLocationPermission
import com.riviem.sunalarm.core.presentation.requestCameraPermission
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import com.riviem.sunalarm.features.home.presentation.homescreen.models.FirstDayOfWeek
import com.riviem.sunalarm.features.settings.presentation.ScrollOneItemDialog
import com.riviem.sunalarm.ui.theme.alarmColor
import com.riviem.sunalarm.ui.theme.textColor
import com.riviem.sunalarm.ui.theme.timePickerBackgroundColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.await

@Composable
fun TimePickerScreen(
    alarm: AlarmUIModel,
    onSaveClick: (AlarmUIModel) -> Unit,
    onCancelClick: () -> Unit,
    firstDayOfWeek: FirstDayOfWeek
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = androidx.compose.ui.platform.LocalContext.current as MainActivity
    var showColorPicker by remember { mutableStateOf(false) }
    var showSoundAlarmPicker by remember { mutableStateOf(false) }
    var newAlarm by remember { mutableStateOf(alarm) }
    val selectedHour by remember { mutableIntStateOf(alarm.ringTime.hour) }
    val selectedMinute by remember { mutableIntStateOf(alarm.ringTime.minute) }
    var newColor by remember { mutableStateOf(alarm.color) }
    val boxTransparency by animateFloatAsState(
        targetValue = if (showSoundAlarmPicker) 0.07f else 1f, label = ""
    )
    val coroutineScope = rememberCoroutineScope()
    var selectedMinutesUntilSoundAlarmBeforeSaving by remember { mutableIntStateOf(alarm.minutesUntilSoundAlarm) }
    var timeMatchesSunrise by remember { mutableStateOf(false) }
    var sunriseTime by remember { mutableStateOf(Pair(0, 0)) }

    Box(modifier = Modifier
        .background(
            color = timePickerBackgroundColor
        )
        .fillMaxSize()
        .graphicsLayer {
            alpha = boxTransparency
        }) {
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
                selectedHour = selectedHour,
                selectedMinute = selectedMinute,
                timeMatchesSunrise = timeMatchesSunrise,
                sunRiseTime = sunriseTime
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
                },
                flashlightActivated = newAlarm.flashlight,
                onFlashlightToggleClicked = {
                    if (hasCameraPermission(context = context)) {
                        newAlarm = newAlarm.copy(
                            flashlight = !newAlarm.flashlight
                        )
                    } else {
                        requestCameraPermission(activity = activity)
                    }
                },
                onSoundAlarmToggleClicked = {
                    if (newAlarm.soundAlarmEnabled) {
                        newAlarm = newAlarm.copy(
                            soundAlarmEnabled = false
                        )
                        showSoundAlarmPicker = false
                    } else {
                        showSoundAlarmPicker = true
                    }
                },
                firstDayOfWeek = firstDayOfWeek,
                onSunriseButtonClicked = {
                    if (timeMatchesSunrise) {
                        timeMatchesSunrise = false
                        return@LightAlarmConfiguration
                    }
                    if (!checkLocationIsEnabled(context = context)) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.location_is_disabled),
                            Toast.LENGTH_SHORT
                        ).show()
                        timeMatchesSunrise = false
                        return@LightAlarmConfiguration
                    }
                    if (!hasLocationPermission(context = context)) {
                        checkAndRequestLocationPermission(activity = activity)
                        timeMatchesSunrise = false
                    } else {
                        coroutineScope.launch {
                            val coordinates = getCoordinates(activity)
                            if (coordinates != null) {
                                try {
                                    val response =
                                        RetrofitInstance.sunriseApiService.getSunriseTime(
                                            coordinates.latitude,
                                            -coordinates.longitude
                                        ).await()
                                    sunriseTime = extractHourAndMinute(response.results.sunrise)
                                    newAlarm = newAlarm.copy(
                                        ringTime = newAlarm.ringTime.withHour(sunriseTime.first)
                                    )
                                    newAlarm = newAlarm.copy(
                                        ringTime = newAlarm.ringTime.withMinute(sunriseTime.second)
                                    )
                                    timeMatchesSunrise = true
                                } catch (e: Exception) {
                                    timeMatchesSunrise = false
                                    Toast.makeText(
                                        context,
                                        context.resources.getString(R.string.error_getting_sunrise_time_maybe_internet_is_off),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                timeMatchesSunrise = false
                            }
                        }
                    }
                },
                onSoundAlarmModalClicked = {
                    showSoundAlarmPicker = true
                },
                timeMatchesSunrise = timeMatchesSunrise
            )
            CancelAndSaveButtons(onCancelClick, onSaveClick, newAlarm)
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
        AnimatedVisibility(visible = showSoundAlarmPicker) {
            ScrollOneItemDialog(
                modifier = Modifier
                    .fillMaxWidth(),
                onDismissRequest = {
                    newAlarm = newAlarm.copy(
                        soundAlarmEnabled = false
                    )
                    showSoundAlarmPicker = false
                },
                onSaveClicked = {
                    newAlarm = newAlarm.copy(
                        minutesUntilSoundAlarm = it,
                        soundAlarmEnabled = true
                    )
                    showSoundAlarmPicker = false
                },
                length = Constants.MINUTES_UNTIL_SOUND_ALARM_INTERVAL,
                title = stringResource(
                    R.string.sound_alarm_after,
                    selectedMinutesUntilSoundAlarmBeforeSaving
                ),
                startScrollIndex = newAlarm.minutesUntilSoundAlarm,
                onSelectedValue = {
                    selectedMinutesUntilSoundAlarmBeforeSaving = it
                }
            )
        }
    }
}

@Composable
private fun CancelAndSaveButtons(
    onCancelClick: () -> Unit,
    onSaveClick: (AlarmUIModel) -> Unit,
    newAlarm: AlarmUIModel
) {
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
                onSaveClick(newAlarm)
            }
        )
    }
}

@Composable
fun CancelButton(
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit,
) {
    ButtonCustom(
        onClick = { onCancelClick() },
        modifier = modifier
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
            color = textColor
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
            color = textColor
        )
    }
}

@Composable
fun LightAlarmConfiguration(
    modifier: Modifier = Modifier,
    alarm: AlarmUIModel,
    onDayClicked: (Day) -> Unit,
    onChooseColorClicked: () -> Unit,
    onAlarmNameChange: (String) -> Unit,
    flashlightActivated: Boolean,
    onFlashlightToggleClicked: () -> Unit,
    onSoundAlarmToggleClicked: () -> Unit,
    onSoundAlarmModalClicked: () -> Unit,
    firstDayOfWeek: FirstDayOfWeek,
    onSunriseButtonClicked: () -> Unit,
    timeMatchesSunrise: Boolean
) {
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var modalTitle by remember { mutableStateOf(context.getString(R.string.every_day)) }
    modalTitle = updateModalTitle(alarm.days, context)

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .background(
                color = alarmColor,
                shape = MaterialTheme.shapes.extraLarge
            )
            .fillMaxWidth()
    ) {
        Text(
            text = modalTitle,
            fontSize = 18.sp,
            color = textColor,
            modifier = Modifier
                .padding(start = 32.dp, top = 25.dp)
        )
        SelectDays(
            alarm = alarm,
            onDayClicked = {
                onDayClicked(it)
            },
            firstDayOfWeek = firstDayOfWeek
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
            modifier = Modifier,
            onChooseColorClicked = onChooseColorClicked,
            color = alarm.color
        )
        SettingToggle(
            modifier = Modifier,
            checked = flashlightActivated,
            onClick = onFlashlightToggleClicked,
            title = stringResource(R.string.flashlight),
            subtitle = stringResource(R.string.alarm_rings_with_flashlight),
            startIcon = if (flashlightActivated) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
            startIconColor = textColor,
        )
        val soundAlarmModifier = if (alarm.soundAlarmEnabled) {
            Modifier
                .clickable {
                    onSoundAlarmModalClicked()
                }
        } else {
            Modifier
        }
        SettingToggle(
            modifier = soundAlarmModifier,
            checked = alarm.soundAlarmEnabled,
            onClick = onSoundAlarmToggleClicked,
            title = stringResource(R.string.sound_alarm),
            subtitle = stringResource(
                R.string.sound_alarm_after_set_minutes,
                alarm.minutesUntilSoundAlarm
            ),
            startIcon = if (alarm.soundAlarmEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
            startIconColor = textColor,
        )
        SettingToggle(
            modifier = Modifier
                .padding(bottom = 20.dp),
            onClick = onSunriseButtonClicked,
            title = stringResource(R.string.sunrise),
            subtitle = stringResource(R.string.set_alarm_to_sunrise),
            startIcon = Icons.Filled.WbSunny,
            startIconColor = textColor,
            checked = timeMatchesSunrise
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
            fontSize = 18.sp,
            color = textColor,
            ),
    )
}

@Composable
fun SelectDays(
    modifier: Modifier = Modifier,
    alarm: AlarmUIModel,
    onDayClicked: (Day) -> Unit,
    firstDayOfWeek: FirstDayOfWeek
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when (firstDayOfWeek) {
            FirstDayOfWeek.MONDAY -> {
                alarm.days.forEach { dayUIModel ->
                    CheckboxDay(
                        day = dayUIModel,
                        isSelected = dayUIModel.isSelected,
                        onDayClicked = onDayClicked
                    )
                }
            }

            FirstDayOfWeek.SUNDAY -> {
                alarm.days[6].let { dayUIModel ->
                    CheckboxDay(
                        day = dayUIModel,
                        isSelected = dayUIModel.isSelected,
                        onDayClicked = onDayClicked
                    )
                }
                alarm.days.take(6).forEach { dayUIModel ->
                    CheckboxDay(
                        day = dayUIModel,
                        isSelected = dayUIModel.isSelected,
                        onDayClicked = onDayClicked
                    )
                }
            }
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
    val mutableInteractionSource = remember { MutableInteractionSource() }

    val newModifier = modifier
        .size(circleSize)
        .clickable(
            interactionSource = mutableInteractionSource,
            indication = null
        ) {
            onDayClicked(day)
        }
        .border(
            width = animatedBorderWidth,
            color = textColor.copy(alpha = animatedAlpha),
            shape = CircleShape
        )

    Box(
        modifier = newModifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.letter,
            fontSize = 14.sp,
            color = textColor
        )
    }
}


@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit,
) {
    ButtonCustom(
        onClick = { onSaveClick() },
        modifier = modifier
    ) {
        Text(text = stringResource(id = R.string.save))
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
    timeMatchesSunrise: Boolean,
    sunRiseTime: Pair<Int, Int>,
) {
    val hoursState = rememberLazyListState()
    val minutesState = rememberLazyListState()

    val hours = infiniteHours.take(480).map { if (it < 10) "0$it" else it.toString() }.toList()
    val minutes = infiniteMinutes.take(1200).map { if (it < 10) "0$it" else it.toString() }.toList()

    println("vladlog: selectedHour on picker: $selectedHour")

    LaunchedEffect(key1 = selectedHour, selectedMinute) {
        hoursState.scrollToItem(index = 240 + selectedHour)
        minutesState.scrollToItem(index = 600 + selectedMinute)
        delay(50L)
        hoursState.animateScrollToItem(index = 240 + selectedHour - 2)
        minutesState.animateScrollToItem(index = 600 + selectedMinute - 1)
    }

    LaunchedEffect(key1 = timeMatchesSunrise) {
        if (timeMatchesSunrise) {
            hoursState.scrollToItem(index = 240 + sunRiseTime.first)
            minutesState.scrollToItem(index = 600 + sunRiseTime.second)
            delay(50L)
            hoursState.animateScrollToItem(index = 240 + sunRiseTime.first - 2)
            minutesState.animateScrollToItem(index = 600 + sunRiseTime.second - 1)
        }
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
                color = textColor,
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
fun TransparentRectangle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .background(
                color = timePickerBackgroundColor.copy(alpha = 0.8f),
            )
    )
}

@Composable
fun TransparentRectangleModal(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .fillMaxWidth()
            .background(
                color = alarmColor.copy(alpha = 0.8f),
            )
    )
}

@Composable
fun TimeScrollItem(
    modifier: Modifier = Modifier,
    time: String
) {
    Text(
        text = time,
        color = textColor,
        fontSize = 50.sp,
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun SettingToggle(
    modifier: Modifier,
    startIcon: ImageVector,
    startIconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(top = 25.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            imageVector = startIcon,
            contentDescription = null,
            modifier = modifier
                .size(30.dp),
            colorFilter = ColorFilter.tint(startIconColor)
        )
        Column(
            modifier = modifier
                .padding(start = 16.dp, end = 8.dp)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                color = textColor
            )
        }

        Spacer(modifier = modifier.weight(0.05f))
        SwitchCustom(
            modifier = modifier
                .padding(end = 8.dp),
            checked = checked,
            onCheckedChange = { onClick() },
        )
    }
}

@Composable
fun ChooseScreenColor(
    modifier: Modifier = Modifier,
    onChooseColorClicked: () -> Unit,
    color: Color
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Row(
        modifier = modifier
            .padding(top = 25.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            imageVector = Icons.Filled.Palette,
            contentDescription = null,
            modifier = modifier
                .size(30.dp),
            colorFilter = ColorFilter.tint(textColor)
        )
        Column(
            modifier = modifier
                .padding(start = 16.dp, end = 8.dp)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = context.getString(R.string.screen_color),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = stringResource(R.string.color_of_the_light_alarm_screen),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                color = textColor
            )
        }
        Box(
            modifier = Modifier
                .clickable { onChooseColorClicked() }
                .padding(start = 10.dp, end = 20.dp)
                .size(30.dp)
                .background(
                    shape = CircleShape,
                    color = color,
                )
                .border(
                    width = 1.dp,
                    color = textColor.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        )
    }
}

