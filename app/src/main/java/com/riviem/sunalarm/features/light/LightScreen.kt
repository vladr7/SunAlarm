package com.riviem.sunalarm.features.light

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
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
import com.riviem.sunalarm.ui.theme.Purple40

@Composable
fun LightScreen(
    viewModel: LightViewModel = hiltViewModel(),
    createdTimestamp: Int,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = androidx.compose.ui.platform.LocalContext.current as android.app.Activity

    var showContent by remember { mutableStateOf(false) }
    val lightModifier = if (showContent) Modifier
        .background(
            color = state.selectedAlarm?.color ?: Color.DarkGray
        )
    else Modifier
        .clickable {
            showContent = true
        }
        .background(
            color = state.selectedAlarm?.color ?: Color.Yellow
        )

    LaunchedEffect(key1 = Unit) {
        viewModel.getAlarmById(createdTimestampId = createdTimestamp)
    }

    LaunchedEffect(key1 = state.brightnessSettingUI.brightness) {
        setBrightness(context, state.brightnessSettingUI.brightness)
    }

    LaunchedEffect(key1 = state.selectedAlarm?.flashlight) {
        if(state.selectedAlarm == null) return@LaunchedEffect
        if(state.selectedAlarm?.flashlight == true) {
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
        AnimatedVisibility(
            visible = showContent,
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
                onDismissClick = {
                    state.selectedAlarm?.let {
                        viewModel.stopAlarm(it, context = context)
                    }
                    activity.finishAffinity()
                }
            )
        }
    }
}

@Composable
private fun LightScreenContent(
    onSnoozeClick: () -> Unit,
    onDismissClick: () -> Unit,
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
            Text(
                text = stringResource(id = R.string.snooze),
                fontSize = 70.sp,
            )
        }
        Button(
            onClick = onDismissClick,
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
                text = stringResource(R.string.dismiss),
                fontSize = 70.sp,
            )
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
