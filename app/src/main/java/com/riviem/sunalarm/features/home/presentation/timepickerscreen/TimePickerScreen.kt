package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.riviem.sunalarm.MainActivity
import com.riviem.sunalarm.R
import com.riviem.sunalarm.core.Constants
import com.riviem.sunalarm.core.Constants.hours
import com.riviem.sunalarm.core.Constants.minutes
import com.riviem.sunalarm.core.presentation.ButtonCustom
import com.riviem.sunalarm.core.presentation.PermissionDialog
import com.riviem.sunalarm.core.presentation.SwitchCustom
import com.riviem.sunalarm.core.presentation.checkLocationIsEnabled
import com.riviem.sunalarm.core.presentation.hasCameraPermission
import com.riviem.sunalarm.core.presentation.hasLocationPermission
import com.riviem.sunalarm.core.presentation.navigateToSettings
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import com.riviem.sunalarm.features.home.presentation.homescreen.models.FirstDayOfWeek
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.models.HourMinute
import com.riviem.sunalarm.features.settings.presentation.CancelSaveButtons
import com.riviem.sunalarm.features.settings.presentation.ScrollOneItemDialog
import com.riviem.sunalarm.ui.theme.alarmColor
import com.riviem.sunalarm.ui.theme.textColor
import com.riviem.sunalarm.ui.theme.timePickerBackgroundColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TimePickerScreen(
    alarm: AlarmUIModel,
    onSaveClick: (AlarmUIModel) -> Unit,
    onCancelClick: () -> Unit,
    firstDayOfWeek: FirstDayOfWeek,
    viewModel: TimePickerViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = androidx.compose.ui.platform.LocalContext.current as MainActivity
    val state by viewModel.state.collectAsStateWithLifecycle()
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
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                coroutineScope.launch {
                    val sunriseTime = viewModel.getSunriseTime(activity)
                    sunriseTime?.let {
                        newAlarm = newAlarm.copy(
                            ringTime = newAlarm.ringTime
                                .withHour(it.hour)
                                .withMinute(it.minute),
                            isAutoSunriseEnabled = true
                        )
                        viewModel.showSunriseFeatureEnabledToast()
                    }
                }
            } else {
                newAlarm = newAlarm.copy(
                    isAutoSunriseEnabled = false
                )
            }
        }
    )
    val flashlightPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            newAlarm = if (isGranted) {
                newAlarm.copy(
                    flashlight = true
                )
            } else {
                newAlarm.copy(
                    flashlight = false
                )
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.initSunriseTime(alarm, activity)
    }

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
                isAutoSunriseEnabled = newAlarm.isAutoSunriseEnabled,
                hourMinute = state.sunriseTime
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
                        viewModel.setShowFlashlightPermissionDialog(true)
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
                    if (newAlarm.isAutoSunriseEnabled) {
                        newAlarm = newAlarm.copy(
                            isAutoSunriseEnabled = false
                        )
                        viewModel.showSunriseFeatureDisabledToast()
                        return@LightAlarmConfiguration
                    }
                    if (!checkLocationIsEnabled(context = context)) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.location_is_disabled),
                            Toast.LENGTH_SHORT
                        ).show()
                        newAlarm = newAlarm.copy(
                            isAutoSunriseEnabled = false
                        )
                        return@LightAlarmConfiguration
                    }
                    if (!hasLocationPermission(context = context)) {
                        viewModel.setShowLocationPermissionDialog(true)
                        newAlarm = newAlarm.copy(
                            isAutoSunriseEnabled = false
                        )
                    } else {
                        coroutineScope.launch {
                            val sunriseTime = viewModel.getSunriseTime(activity)
                            sunriseTime?.let {
                                newAlarm = newAlarm.copy(
                                    ringTime = newAlarm.ringTime
                                        .withHour(it.hour)
                                        .withMinute(it.minute),
                                    isAutoSunriseEnabled = true
                                )
                                viewModel.showSunriseFeatureEnabledToast()
                            }
                        }
                    }
                },
                onSoundAlarmModalClicked = {
                    showSoundAlarmPicker = true
                },
                isAutoSunriseEnabled = newAlarm.isAutoSunriseEnabled
            )
            CancelAndSaveButtons(
                onCancelClick,
                onSaveClick = {
                    if (it.isAutoSunriseEnabled && state.sunriseTime?.hour != it.ringTime.hour && state.sunriseTime?.minute != it.ringTime.minute) {
                        viewModel.showSunriseFeatureDisabledToast()
                        onSaveClick(it.copy(isAutoSunriseEnabled = false))
                    } else {
                        onSaveClick(it)
                    }
                },
                newAlarm
            )
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
                selectedColor = newColor
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

    DisplayToasts(state, context, viewModel)

    if(state.showLocationPermissionDialog) {
        PermissionDialog(
            title = stringResource(R.string.location_permission),
            description = stringResource(R.string.app_needs_location_permission_to_get_sunrise_time),
            onDismissRequest = {
                viewModel.setShowLocationPermissionDialog(false)
            },
            onConfirmClicked = {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    navigateToSettings(context = context, activity = activity)
                    viewModel.setShowLocationPermissionDialog(false)
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    viewModel.setShowLocationPermissionDialog(false)
                }
            }
        )
    }

    if(state.showFlashlightPermissionDialog) {
        PermissionDialog(
            title = stringResource(R.string.camera_permission),
            description = stringResource(R.string.app_needs_camera_permission_to_turn_on_flashlight),
            onDismissRequest = {
                viewModel.setShowFlashlightPermissionDialog(false)
            },
            onConfirmClicked = {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.CAMERA
                    )
                ) {
                    navigateToSettings(context = context, activity = activity)
                    viewModel.setShowFlashlightPermissionDialog(false)
                } else {
                    flashlightPermissionLauncher.launch(Manifest.permission.CAMERA)
                    viewModel.setShowFlashlightPermissionDialog(false)
                }
            }
        )
    }
}

@Composable
private fun DisplayToasts(
    state: TimePickerState,
    context: Context,
    viewModel: TimePickerViewModel
) {
    LaunchedEffect(key1 = state.showInternetOffError) {
        if (state.showInternetOffError == true) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.error_getting_sunrise_time_maybe_internet_is_off),
                Toast.LENGTH_LONG
            ).show()
            viewModel.resetToasts()
        }
    }
    LaunchedEffect(key1 = state.showGeneralError) {
        if (state.showGeneralError == true) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.error_getting_sunrise_time),
                Toast.LENGTH_LONG
            ).show()
            viewModel.resetToasts()
        }
    }
    LaunchedEffect(key1 = state.showSunriseFeatureEnabledToast) {
        val hour = state.sunriseTime?.hour ?: return@LaunchedEffect
        val minute = state.sunriseTime.minute
        if (state.showSunriseFeatureEnabledToast) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.sun_rises_at, hour, minute),
                Toast.LENGTH_LONG
            ).show()
            viewModel.resetToasts()
        }
    }
    LaunchedEffect(key1 = state.showSunriseFeatureDisabledToast) {
        if (state.showSunriseFeatureDisabledToast) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.sunrise_auto_set_disabled),
                Toast.LENGTH_LONG
            ).show()
            viewModel.resetToasts()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
    modifier: Modifier = Modifier,
    onColorChanged: (Color) -> Unit,
    onSaveColorClicked: () -> Unit,
    onCancelColorClicked: () -> Unit,
    selectedColor: Color
) {
    AlertDialog(
        modifier = modifier
            .background(
                color = alarmColor,
                shape = RoundedCornerShape(8.dp)
            ),
        onDismissRequest = onCancelColorClicked,
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
        content = {
            Column(
                modifier = modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ColorPicker(onColorChanged = onColorChanged)
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = textColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = selectedColor,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .height(20.dp)
                        .width(70.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CancelSaveButtons(
                        onSaveClicked = { onSaveColorClicked() },
                        onCancelClicked = { onCancelColorClicked() }
                    )
                }
            }
        }
    )
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
    isAutoSunriseEnabled: Boolean
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
            fontSize = 17.sp,
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
            subtitle = stringResource(R.string.auto_set_alarm_to_sunrise_time),
            startIcon = Icons.Filled.WbSunny,
            startIconColor = textColor,
            checked = isAutoSunriseEnabled
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
    isAutoSunriseEnabled: Boolean,
    hourMinute: HourMinute?,
) {
    val hoursState = rememberLazyListState()
    val minutesState = rememberLazyListState()

    LaunchedEffect(key1 = selectedHour, selectedMinute) {
        hoursState.scrollToItem(index = 240 + selectedHour)
        minutesState.scrollToItem(index = 600 + selectedMinute)
        delay(50L)
        hoursState.animateScrollToItem(index = 240 + selectedHour - 2)
        minutesState.animateScrollToItem(index = 600 + selectedMinute - 1)
    }

    LaunchedEffect(key1 = isAutoSunriseEnabled) {
        if (isAutoSunriseEnabled && hourMinute != null) {
            hoursState.scrollToItem(index = 240 + hourMinute.hour)
            minutesState.scrollToItem(index = 600 + hourMinute.minute)
            delay(50L)
            hoursState.animateScrollToItem(index = 240 + hourMinute.hour - 2)
            minutesState.animateScrollToItem(index = 600 + hourMinute.minute - 1)
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

