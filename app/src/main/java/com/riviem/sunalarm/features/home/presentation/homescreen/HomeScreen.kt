package com.riviem.sunalarm.features.home.presentation.homescreen

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.annotations.concurrent.Background
import com.riviem.sunalarm.R
import com.riviem.sunalarm.core.presentation.PermissionDialog
import com.riviem.sunalarm.core.presentation.SwitchCustom
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.Day
import com.riviem.sunalarm.features.home.presentation.homescreen.models.FirstDayOfWeek
import com.riviem.sunalarm.features.home.presentation.homescreen.models.allDoorsSelected
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.TimePickerScreen
import com.riviem.sunalarm.ui.theme.alarmColor
import com.riviem.sunalarm.ui.theme.backgroundColor
import com.riviem.sunalarm.ui.theme.textColor
import com.riviem.sunalarm.ui.theme.timePickerBackgroundColor
import kotlinx.coroutines.delay
import java.time.ZonedDateTime


@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onAlarmClick: () -> Unit,
    onSaveOrDiscardClick: () -> Unit,
) {
    Background()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.getFirstDayOfWeek()
    }

    var showPermissionDialog by remember {
        mutableStateOf(false)
    }
    if (showPermissionDialog) {
        PermissionDialog(
            title = "Flashlight requires camera permission",
            description = "Please allow camera permission to use flashlight",
            onDismissRequest = { showPermissionDialog = false },
            onConfirmClicked = { showPermissionDialog = false },
        )
    }

    AnimatedVisibility(
        visible = !state.showTimePickerScreen && state.alarms != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        HomeScreen(
            context = context,
            onAlarmClick = { alarm ->
                if (!alarm.isExpandedForEdit) {
                    viewModel.onAlarmClick(alarm)
                    onAlarmClick()
                } else {
                    viewModel.onAlarmClick(alarm)
                }
            },
            alarms = state.alarms ?: emptyList(),
            onAddNewAlarmClick = {
                viewModel.onAddNewAlarmClick()
            },
            onAlarmCheckChanged = { checked, alarm ->
                viewModel.onAlarmCheckChanged(checked, alarm, context)
            },
            title = state.title,
            subtitle = state.subtitle,
            firstDayOfWeek = state.firstDayOfWeek,
            onAlarmLongPress = {
                viewModel.onAlarmLongPress(it)
                showPermissionDialog = !showPermissionDialog
            },
            onDeleteAlarmClick = {
                viewModel.onDeleteAlarmClick(it, context)
            }
        )
    }
    AnimatedVisibility(
        visible = state.showTimePickerScreen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        TimePickerScreen(
            alarm = state.selectedAlarm,
            onSaveClick = { alarm ->
                viewModel.onSaveAlarmClick(alarm, context)
                onSaveOrDiscardClick()
            },
            onCancelClick = {
                viewModel.onCancelAlarmClick()
                onSaveOrDiscardClick()
            },
            firstDayOfWeek = state.firstDayOfWeek
        )
    }
    LaunchedEffect(key1 = state.showNextAlarmTimeToast) {
        if (state.showNextAlarmTimeToast) {
            Toast.makeText(context, state.title, Toast.LENGTH_SHORT).show()
            viewModel.onShowNextAlarmTimeToastDone()
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
    onAlarmClick: (AlarmUIModel) -> Unit,
    alarms: List<AlarmUIModel>,
    onAddNewAlarmClick: () -> Unit,
    onAlarmCheckChanged: (Boolean, AlarmUIModel) -> Unit,
    title: String,
    subtitle: String,
    firstDayOfWeek: FirstDayOfWeek,
    onAlarmLongPress: (AlarmUIModel) -> Unit,
    onDeleteAlarmClick: (AlarmUIModel) -> Unit
) {
    GradientBackgroundScreen {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (alarms.isEmpty()) {
                    NoAlarmContent(
                        context = context,
                        onAddNewAlarmClick = {
                            onAddNewAlarmClick()
                        }
                    )
                } else {
                    Box(
                        modifier = modifier
                            .padding(top = 50.dp)
                            .weight(0.30f)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        HomeScreenTitle(
                            modifier = modifier.fillMaxWidth(),
                            title = title,
                            subtitle = subtitle
                        )
                    }
                    Column(
                        modifier = modifier.weight(0.70f)
                    ) {
                        AddNewAlarmButton(
                            onClick = {
                                onAddNewAlarmClick()
                            }
                        )
                        AlarmsList(
                            onAlarmClick = onAlarmClick,
                            onCheckedChange = { checked, alarm ->
                                onAlarmCheckChanged(checked, alarm)
                            },
                            modifier = modifier.fillMaxWidth(),
                            alarms = alarms,
                            firstDayOfWeek = firstDayOfWeek,
                            onAlarmLongPress = {
                                onAlarmLongPress(it)
                            },
                            onDeleteAlarmClick = {
                                onDeleteAlarmClick(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoAlarmContent(
    context: Context,
    onAddNewAlarmClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                imageVector = Icons.Filled.AlarmOff, contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = context.getString(R.string.no_alarms),
                fontSize = 30.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize(),
                lineHeight = 36.sp,
            )
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = { onAddNewAlarmClick() },
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = alarmColor,
                        shape = CircleShape
                    ),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    contentColor = textColor,
                    containerColor = alarmColor,
                )
            ) {
                Image(
                    imageVector = Icons.Filled.Add, contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.padding(15.dp))
        }
    }
}

@Composable
fun HomeScreenTitle(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
) {
    val context = LocalContext.current
    var showContent by remember {
        mutableStateOf(false)
    }
    var newTitle by remember {
        mutableStateOf(title)
    }
    var newSubtitle by remember {
        mutableStateOf(subtitle)
    }
    LaunchedEffect(key1 = title) {
        showContent = false
        delay(500L)
        newTitle = title
        newSubtitle = subtitle
        showContent = true
    }

    Column(
        modifier = modifier.padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            Text(
                text = newTitle,
                fontSize = 30.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize(),
                lineHeight = 36.sp,
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        AnimatedVisibility(
            visible = showContent,
        ) {
            Text(
                text = newSubtitle,
                fontSize = 16.sp,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize(),
                fontWeight = FontWeight.Bold
            )
        }
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
            tint = textColor
        )
    }
}

@Composable
fun AlarmsList(
    modifier: Modifier = Modifier,
    onAlarmClick: (AlarmUIModel) -> Unit,
    onCheckedChange: (Boolean, AlarmUIModel) -> Unit,
    alarms: List<AlarmUIModel>,
    firstDayOfWeek: FirstDayOfWeek,
    onAlarmLongPress: (AlarmUIModel) -> Unit,
    onDeleteAlarmClick: (AlarmUIModel) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        content = {
            items(
                items = alarms,
                key = { alarm ->
                    alarm.createdTimestamp
                }
            ) { item ->
                AlarmItem(
                    alarm = item,
                    onAlarmClick = {
                        onAlarmClick(item)
                    },
                    onCheckedChange = {
                        onCheckedChange(it, item)
                    },
                    firstDayOfWeek = firstDayOfWeek,
                    onAlarmLongPress = {
                        onAlarmLongPress(item)
                    },
                    onDeleteAlarmClick = {
                        onDeleteAlarmClick(item)
                    }
                )
            }
        }
    )
}

@Composable
fun AlarmItem(
    modifier: Modifier = Modifier,
    alarm: AlarmUIModel,
    onAlarmClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    firstDayOfWeek: FirstDayOfWeek,
    onAlarmLongPress: () -> Unit,
    onDeleteAlarmClick: () -> Unit
) {

        Row(
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            onAlarmLongPress()
                        },
                        onTap = { onAlarmClick() }
                    )
                }
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                .background(
                    color = alarmColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .height(120.dp)
                .fillMaxWidth()
                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlarmNameAndTime(alarm.name, alarm.ringTime)
            Spacer(modifier = Modifier.weight(1f))
            if (!alarm.isExpandedForEdit) {
                AlarmSelectedDays(
                    modifier = modifier,
                    days = alarm.days,
                    firstDayOfWeek = firstDayOfWeek
                )
                AlarmSwitch(
                    modifier = modifier
                        .padding(end = 10.dp),
                    checked = alarm.isOn,
                    onCheckedChange = onCheckedChange
                )
            }
            AnimatedVisibility(visible = alarm.isExpandedForEdit) {
                DeleteAlarmButton(
                    modifier = modifier
                        .padding(end = 30.dp),
                    onClick = onDeleteAlarmClick
                )
            }
    }

}

@Composable
fun DeleteAlarmButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Image(
        modifier = modifier
            .size(30.dp)
            .clickable {
                onClick()
            },
        imageVector = Icons.Filled.Delete, contentDescription = null,
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(textColor)
    )
}

@Composable
private fun AlarmNameAndTime(name: String, time: ZonedDateTime) {
    val hour = if (time.hour < 10) "0${time.hour}" else time.hour
    val minute = if (time.minute < 10) "0${time.minute}" else time.minute
    Column {
        Text(
            text = name,
            fontSize = 17.sp,
            color = textColor,
            modifier = Modifier
                .padding(start = 15.dp)
                .offset(y = (-10).dp)
        )
        Text(
            text = "$hour:$minute",
            fontSize = 30.sp,
            color = textColor,
            modifier = Modifier.padding(start = 18.dp, top = 10.dp)
        )
    }
}

@Composable
fun AlarmSelectedDays(
    modifier: Modifier = Modifier,
    days: List<Day>,
    firstDayOfWeek: FirstDayOfWeek
) {
    if (days.allDoorsSelected()) {
        Text(
            text = stringResource(R.string.every_day), fontSize = 16.sp, color = textColor,
            modifier = modifier
                .padding(end = 10.dp)
        )
    } else {
        Row(
            modifier = modifier
                .padding(end = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when (firstDayOfWeek) {
                FirstDayOfWeek.MONDAY -> {
                    days.forEach { day ->
                        DayWithOrWithoutDot(
                            modifier = modifier
                                .padding(end = 3.dp),
                            day = day.letter,
                            isSelected = day.isSelected
                        )
                    }
                }

                FirstDayOfWeek.SUNDAY -> {
                    DayWithOrWithoutDot(
                        modifier = modifier
                            .padding(end = 3.dp),
                        day = days[6].letter,
                        isSelected = days[6].isSelected
                    )
                    days.subList(0, days.size - 1).forEach { day ->
                        DayWithOrWithoutDot(
                            modifier = modifier
                                .padding(end = 3.dp),
                            day = day.letter,
                            isSelected = day.isSelected
                        )
                    }

                }
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
    Column(
        modifier = modifier,
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Circle, contentDescription = null,
                modifier = modifier
                    .size(6.dp),
                tint = Color.White
            )
        }
        Text(
            text = day, fontSize = 14.sp, color = Color.White,
            modifier = modifier
                .padding(top = 2.dp)
                .alpha(if (isSelected) 1f else 0.5f)
        )
    }
}

@Composable
fun AlarmSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SwitchCustom(
        modifier = modifier,
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        },
    )
}

@Composable
fun GradientBackgroundScreen(
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        timePickerBackgroundColor,
                        Color.White,
                    )
                )
            )
    ) {
        content()
    }
}





