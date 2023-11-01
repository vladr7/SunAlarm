package com.riviem.sunalarm.features.settings.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.sunalarm.MainActivity
import com.riviem.sunalarm.R
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.TimeScrollItem
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.TransparentRectangle
import kotlinx.coroutines.delay

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as MainActivity

    SettingsScreen(
        onSnoozeSelected = {
            println("vladlog: onSnoozeSelected: $it")
            viewModel.setSnoozeLength(it)
        },
        snoozeLength = state.snoozeLength
    )
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onSnoozeSelected: (Int) -> Unit,
    snoozeLength: Int
) {
    var showSnoozeSettingDialog by remember { mutableStateOf(false) }
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
    }
    AnimatedVisibility(visible = showSnoozeSettingDialog) {
        SnoozeSettingDialog(
            modifier = modifier
                .fillMaxWidth(),
            onDismissRequest = {
                showSnoozeSettingDialog = false
            },
            onSaveClicked = {
                showSnoozeSettingDialog = false
            },
            onSnoozeSelected = onSnoozeSelected,
            snoozeLength = snoozeLength
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnoozeSettingDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSaveClicked: () -> Unit,
    onSnoozeSelected: (Int) -> Unit,
    snoozeLength: Int
) {
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
                    text = stringResource(R.string.snooze_length),
                    fontSize = 36.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                ScrollableSnoozePicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    selectedSnooze = snoozeLength,
                    onSnoozeSelected = onSnoozeSelected
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            onDismissRequest()
                        },
                        modifier = Modifier.padding(top = 30.dp)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            onSaveClicked()
                        },
                        modifier = Modifier.padding(top = 30.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ScrollableSnoozePicker(
    modifier: Modifier = Modifier,
    selectedSnooze: Int,
    onSnoozeSelected: (Int) -> Unit
) {
    val snoozeLengths = List(121) { it }

    ScrollableValuePicker(
        modifier = modifier,
        infiniteValues = snoozeLengths,
        onValueSelected = onSnoozeSelected,
        maxSize = snoozeLengths.size,
        startScrollIndex = selectedSnooze
    )
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableValuePicker(
    modifier: Modifier = Modifier,
    infiniteValues: List<Int>,
    onValueSelected: (Int) -> Unit,
    maxSize: Int,
    startScrollIndex: Int
) {
    val valueState = rememberLazyListState()
    val values =
        infiniteValues.take(maxSize).map { if (it < 10) "0$it" else it.toString() }.toList()
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
        TransparentRectangle(
            modifier = Modifier.align(Alignment.TopCenter)
        )
        TransparentRectangle(
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