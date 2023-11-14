package com.riviem.sunalarm.features.light

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riviem.sunalarm.R
import com.riviem.sunalarm.core.presentation.createDismissSoundNotification
import com.riviem.sunalarm.core.presentation.enums.AlarmType
import com.riviem.sunalarm.ui.theme.Purple40


@Composable
fun LightScreen(
    viewModel: LightViewModel = hiltViewModel(),
    createdTimestamp: Int,
    alarmType: AlarmType,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = androidx.compose.ui.platform.LocalContext.current as android.app.Activity
    val mediaPlayer by remember {
        mutableStateOf(
            MediaPlayer.create(
                context,
                Settings.System.DEFAULT_RINGTONE_URI
            )
        )
    }

    var showDismissSnoozeScreen by remember { mutableStateOf(false) }
    val lightModifier = if (showDismissSnoozeScreen) Modifier
        .background(
            color = state.selectedAlarm?.color ?: Color.DarkGray
        )
    else Modifier
        .clickable {
            showDismissSnoozeScreen = true
        }
        .background(
            color = state.selectedAlarm?.color ?: Color.Yellow
        )

    if (alarmType == AlarmType.SOUND) {
        LaunchedEffect(key1 = Unit) {
            playSound(mediaPlayer)
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getAlarmById(createdTimestampId = createdTimestamp, alarmType = alarmType)
    }

    LaunchedEffect(key1 = state.brightnessSettingUI.brightness) {
        setBrightness(context, state.brightnessSettingUI.brightness)
    }

    LaunchedEffect(key1 = state.shouldCreateDismissSoundNotification) {
        if (state.shouldCreateDismissSoundNotification) {
            state.selectedAlarm?.let { alarmUIModel ->
                createDismissSoundNotification(
                    context,
                    alarmUIModel
                )
            }
        }
    }

    LaunchedEffect(key1 = state.selectedAlarm?.flashlight) {
        if (state.selectedAlarm == null) return@LaunchedEffect
        if (state.selectedAlarm?.flashlight == true) {
            try {
                handleFlashlight(context, true)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        } else {
            try {
                handleFlashlight(context, false)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = lightModifier
            .fillMaxSize(),
        verticalArrangement = SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = !showDismissSnoozeScreen) {
            TimeText(
                text = state.currentTime,
            )
        }
        AnimatedVisibility(
            visible = showDismissSnoozeScreen,
            enter = fadeIn(
                tween(1000)
            ),
            exit = fadeOut(
                tween(1000)
            )
        ) {
            LightScreenContent(
                onSnoozeClick = {
                    state.selectedAlarm?.let {
                        viewModel.snoozeAlarm(it, context = context)
                    }
                    activity.finishAffinity()
                },
                onDismissLightClick = {
                    stopSound(mediaPlayer)
                    state.selectedAlarm?.let {
                        viewModel.stopLightAlarm(
                            it,
                            context = context,
                            alarmType = alarmType
                        )
                    }
                    activity.finishAffinity()
                },
                snoozeLength = state.snoozeLength,
                onDismissSoundClick = {
                    stopSound(mediaPlayer)
                    state.selectedAlarm?.let {
                        viewModel.stopSoundAlarm(
                            it,
                            context = context,
                            alarmType = alarmType
                        )
                    }
                },
                showDismissSoundButton = state.showDismissSoundButton
            )
        }
    }
}

@Composable
private fun TimeText(
    text: String,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(25),
            )
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 70.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun LightScreenContent(
    onSnoozeClick: () -> Unit,
    onDismissLightClick: () -> Unit,
    onDismissSoundClick: () -> Unit,
    snoozeLength: Int,
    showDismissSoundButton: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onSnoozeClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
                .weight(0.5f),
            shape = RoundedCornerShape(5),
            colors = ButtonColors(
                containerColor = Purple40,
                contentColor = Color.White,
                disabledContentColor = Color.White,
                disabledContainerColor = Color.Gray
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.snooze),
                    fontSize = 65.sp,
                )
                Text(text = stringResource(R.string.snooze_minutes, snoozeLength), fontSize = 35.sp)
            }
        }
        Button(
            onClick = onDismissLightClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                .weight(0.5f),
            colors = ButtonColors(
                containerColor = Color.Red,
                contentColor = Color.White,
                disabledContentColor = Color.White,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(5)
        ) {
            Text(
                text = stringResource(R.string.dismiss_light_alarm),
                fontSize = 65.sp,
            )
        }
        AnimatedVisibility(visible = showDismissSoundButton) {
            Button(
                onClick = onDismissSoundClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                    .weight(0.5f),
                colors = ButtonColors(
                    containerColor = Color.Red.copy(alpha = 0.5f),
                    contentColor = Color.White,
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(5)
            ) {
                Text(
                    text = stringResource(R.string.dismiss_sound_alarm),
                    fontSize = 40.sp,
                    lineHeight = 40.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

fun setBrightness(context: Context, brightnessValue: Int) {
    val brightness = brightnessValue.coerceIn(0, 255)

    val layoutParams = (context as Activity).window.attributes
    layoutParams.screenBrightness = brightness.toFloat() / 255
    context.window.attributes = layoutParams

    try {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            brightness
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun handleFlashlight(context: Context, on: Boolean) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = cameraManager.cameraIdList[0]
    cameraManager.setTorchMode(cameraId, on)
}

private fun playSound(mediaPlayer: MediaPlayer) {
    mediaPlayer.setOnPreparedListener { mp ->
        mp.start()
    }
    mediaPlayer.setOnCompletionListener { mp ->
        mp.release()
    }
    mediaPlayer.isLooping = true
    mediaPlayer.start()
}

fun stopSound(mediaPlayer: MediaPlayer) {
    if (!mediaPlayer.isPlaying) return
    mediaPlayer.stop()
    mediaPlayer.release()
}


