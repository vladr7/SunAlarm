package com.riviem.sunalarm.features.settings.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.sunalarm.MainActivity
import com.riviem.sunalarm.R
import com.riviem.sunalarm.core.presentation.askBrightnessPermission
import com.riviem.sunalarm.core.presentation.hasBrightnessPermission
import com.riviem.sunalarm.features.home.presentation.homescreen.models.FirstDayOfWeek
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.CancelButton
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.SaveButton
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.TimeScrollItem
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.TransparentRectangleModal
import com.riviem.sunalarm.features.settings.presentation.models.BrightnessSettingUI
import com.riviem.sunalarm.ui.theme.SettingsActivateSwitchButtonColor
import com.riviem.sunalarm.ui.theme.SettingsDisabledSwitchButtonColor
import com.riviem.sunalarm.ui.theme.SettingsDisabledSwitchTrackColor
import com.riviem.sunalarm.ui.theme.SettingsInactiveSwitchButtonColor
import com.riviem.sunalarm.ui.theme.SettingsInactiveSwitchTrackColor
import com.riviem.sunalarm.ui.theme.alarmColor
import com.riviem.sunalarm.ui.theme.textColor
import kotlinx.coroutines.delay

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as MainActivity

    SettingsScreen(
        snoozeLength = state.snoozeLength,
        onBrightnessSaveClicked = {
            viewModel.setBrightnessSettings(it)
        },
        brightnessSettingUI = state.brightnessSettingUI,
        onSelectFirstDayOfWeek = {
            viewModel.setFirstDayOfWeek(it)
        },
        firstDayOfWeek = state.firstDayOfWeek,
        onSnoozeSavedClicked = {
            viewModel.setSnoozeLength(it)
        },
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onSnoozeSavedClicked: (Int) -> Unit,
    snoozeLength: Int,
    onBrightnessSaveClicked: (BrightnessSettingUI) -> Unit,
    brightnessSettingUI: BrightnessSettingUI,
    onSelectFirstDayOfWeek: (FirstDayOfWeek) -> Unit,
    firstDayOfWeek: FirstDayOfWeek,
) {
    val activity = LocalContext.current as MainActivity
    var showSnoozeSettingDialog by remember { mutableStateOf(false) }
    var showBrightnessSettingDialog by remember { mutableStateOf(false) }
    var showFirstDayOfWeekDropdown by remember { mutableStateOf(false) }
    var showSoundAlarmMinutesDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SnoozeSettingButton(
            modifier = modifier.padding(top = 30.dp),
            onClick = {
                showSnoozeSettingDialog = true
            }
        )
        BrightnessSettingButton(
            modifier = modifier.padding(top = 30.dp),
            onClick = {
                if (hasBrightnessPermission(activity)) {
                    showBrightnessSettingDialog = true
                } else {
                    askBrightnessPermission(activity)
                }
            }
        )
        FirstDayOfTheWeek(
            modifier = modifier.padding(top = 30.dp),
            onClick = {
                showFirstDayOfWeekDropdown = true
            },
            firstDay = firstDayOfWeek.fullName
        )
    }
    AnimatedVisibility(visible = showSnoozeSettingDialog) {
        SnoozeSettingDialog(
            modifier = modifier
                .fillMaxWidth(),
            onDismissRequest = {
                showSnoozeSettingDialog = false
            },
            onSaveClicked = {
                onSnoozeSavedClicked(it)
                showSnoozeSettingDialog = false
            },
            length = 61,
            title = stringResource(R.string.snooze_length),
            initialSnoozeLength = snoozeLength
        )
    }
    AnimatedVisibility(visible = showBrightnessSettingDialog) {
        BrightnessSettingDialog(
            modifier = modifier
                .fillMaxWidth(),
            onDismissRequest = {
                showBrightnessSettingDialog = false
            },
            onSaveClicked = {
                onBrightnessSaveClicked(it)
                showBrightnessSettingDialog = false
            },
            brightnessSettingUI = brightnessSettingUI,
        )
    }
    AnimatedVisibility(visible = showFirstDayOfWeekDropdown) {
        FirstDayOfWeekDropdown(
            modifier = modifier,
            onDismissRequest = {
                showFirstDayOfWeekDropdown = false
            },
            onClick = {
                onSelectFirstDayOfWeek(it)
                showFirstDayOfWeekDropdown = false
            },
            expanded = showFirstDayOfWeekDropdown
        )
    }
}

@Composable
fun FirstDayOfWeekDropdown(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    onClick: (FirstDayOfWeek) -> Unit,
    expanded: Boolean
) {
    Column(
        modifier = modifier,
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismissRequest() },
            content = {
                DropdownMenuItem(text = {
                    Text(text = FirstDayOfWeek.MONDAY.fullName)
                }, onClick = { onClick(FirstDayOfWeek.MONDAY) })
                DropdownMenuItem(text = {
                    Text(text = FirstDayOfWeek.SUNDAY.fullName)
                }, onClick = { onClick(FirstDayOfWeek.SUNDAY) })
            }
        )
    }
}

@Composable
fun FirstDayOfTheWeek(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    firstDay: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onClick, modifier = modifier) {
            Text(text = stringResource(R.string.first_day_of_the_week))
        }
        Text(text = firstDay)
    }

}

@Composable
fun BrightnessSettingButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.brightness))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrightnessSettingDialog(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    onSaveClicked: (BrightnessSettingUI) -> Unit,
    brightnessSettingUI: BrightnessSettingUI
) {
    var brightnessSliderValue by remember { mutableIntStateOf(brightnessSettingUI.brightness) }
    var brightnessGraduallySliderValue by remember { mutableIntStateOf(brightnessSettingUI.brightnessGraduallyMinutes) }

    AlertDialog(
        modifier = modifier
            .height(500.dp),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
        content = {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.brightness),
                    fontSize = 36.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Spacer(modifier = Modifier.height(50.dp))
                BrightnessSlider(
                    text = stringResource(R.string.brightness),
                    sliderValue = brightnessSliderValue,
                    onSliderValueChanged = {
                        brightnessSliderValue = it
                    }
                )
                Spacer(modifier = Modifier.height(50.dp))
                BrightnessGraduallySlider(
                    text = stringResource(R.string.increase_brightness_gradually_over_time),
                    sliderValue = brightnessGraduallySliderValue,
                    onSliderValueChanged = {
                        brightnessGraduallySliderValue = it
                    }
                )
                CancelSaveButtons(
                    onCancelClicked = {
                        onDismissRequest()
                    },
                    onSaveClicked = {
                        onSaveClicked(
                            BrightnessSettingUI(
                                brightness = brightnessSliderValue,
                                brightnessGraduallyMinutes = brightnessGraduallySliderValue
                            )
                        )
                    }
                )
            }
        }
    )
}

@Composable
private fun CancelSaveButtons(
    onSaveClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    Row {
        CancelButton {
            onCancelClicked()
        }
        SaveButton {
            onSaveClicked()
        }
    }
}

@Composable
private fun BrightnessSlider(
    text: String,
    sliderValue: Int,
    onSliderValueChanged: (Int) -> Unit
) {
    Text(
        text = text,
        fontSize = 16.sp,
    )
    Slider(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
        value = sliderValue.toFloat(),
        onValueChange = { newValue ->
            onSliderValueChanged(newValue.toInt())
        },
        valueRange = 1f..100f,
        steps = 100,
        colors = SliderColors(
            thumbColor = SettingsActivateSwitchButtonColor,
            activeTrackColor = SettingsActivateSwitchButtonColor,
            activeTickColor = SettingsActivateSwitchButtonColor.copy(alpha = 0.7f),
            inactiveTrackColor = SettingsInactiveSwitchButtonColor,
            inactiveTickColor = SettingsInactiveSwitchTrackColor,
            disabledThumbColor = SettingsDisabledSwitchButtonColor,
            disabledActiveTrackColor = SettingsDisabledSwitchTrackColor,
            disabledActiveTickColor = SettingsDisabledSwitchTrackColor.copy(alpha = 0.7f),
            disabledInactiveTrackColor = SettingsDisabledSwitchButtonColor,
            disabledInactiveTickColor = SettingsDisabledSwitchTrackColor
        ),
    )
}

@Composable
private fun BrightnessGraduallySlider(
    text: String,
    sliderValue: Int,
    onSliderValueChanged: (Int) -> Unit
) {
    Text(
        text = text,
        fontSize = 16.sp,
    )
    Slider(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
        value = sliderValue.toFloat(),
        onValueChange = { newValue ->
            onSliderValueChanged(newValue.toInt())
        },
        valueRange = 1f..100f,
        steps = 100,
        colors = SliderColors(
            thumbColor = SettingsActivateSwitchButtonColor,
            activeTrackColor = SettingsActivateSwitchButtonColor,
            activeTickColor = SettingsActivateSwitchButtonColor.copy(alpha = 0.7f),
            inactiveTrackColor = SettingsInactiveSwitchButtonColor,
            inactiveTickColor = SettingsInactiveSwitchTrackColor,
            disabledThumbColor = SettingsDisabledSwitchButtonColor,
            disabledActiveTrackColor = SettingsDisabledSwitchTrackColor,
            disabledActiveTickColor = SettingsDisabledSwitchTrackColor.copy(alpha = 0.7f),
            disabledInactiveTrackColor = SettingsDisabledSwitchButtonColor,
            disabledInactiveTickColor = SettingsDisabledSwitchTrackColor
        ),
    )
}

@Composable
fun SnoozeSettingDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSaveClicked: (Int) -> Unit,
    length: Int,
    title: String,
    initialSnoozeLength: Int
) {
    ScrollOneItemDialog(
        modifier,
        onDismissRequest,
        title,
        length,
        onSaveClicked,
        startScrollIndex = initialSnoozeLength,
        onSelectedValue = {}
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ScrollOneItemDialog(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    title: String,
    length: Int,
    onSaveClicked: (Int) -> Unit,
    startScrollIndex: Int,
    onSelectedValue: (Int) -> Unit
) {
    var selectedValue by remember { mutableIntStateOf(startScrollIndex) }

    LaunchedEffect(key1 = startScrollIndex) {
        selectedValue = startScrollIndex
    }

    AlertDialog(
        modifier = modifier
            .background(
                color = alarmColor,
                shape = RoundedCornerShape(8.dp)
            )
            .height(500.dp),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
        content = {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = textColor
                )
                ScrollableValuePicker(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    onValueSelected = {
                        onSelectedValue(it)
                        selectedValue = it
                    },
                    startScrollIndex = selectedValue,
                    length = length
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CancelSaveButtons(
                        onSaveClicked = { onSaveClicked(selectedValue) },
                        onCancelClicked = { onDismissRequest() }
                    )
                }
            }
        }
    )
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableValuePicker(
    modifier: Modifier = Modifier,
    onValueSelected: (Int) -> Unit,
    startScrollIndex: Int,
    length: Int
) {
    val list = List(length) { it }
    val valueState = rememberLazyListState()
    val values =
        list.take(list.size).map { if (it < 10) "0$it" else it.toString() }.toList()
    val localDensity = LocalDensity.current

    LaunchedEffect(key1 = Unit) {
        valueState.scrollToItem(index = startScrollIndex)
        delay(50L)
        valueState.animateScrollToItem(index = startScrollIndex - 1)
    }

    LaunchedEffect(
        key1 = valueState.firstVisibleItemIndex,
        valueState.firstVisibleItemScrollOffset
    ) {
        val index = valueState.firstVisibleItemIndex
        val offset = valueState.firstVisibleItemScrollOffset
        val actualIndex = if (offset > with(localDensity) { 50.dp.toPx() }) index + 1 else index
        val value = values[actualIndex]
        onValueSelected((value.toInt() + 1))
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            modifier = modifier,
            content = {
                items(values) { value ->
                    TimeScrollItem(
                        time = value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            },
            state = valueState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = valueState)
        )
        TransparentRectangleModal(
            modifier = Modifier.align(Alignment.TopCenter)
        )
        TransparentRectangleModal(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SnoozeSettingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.snooze_length))
    }
}