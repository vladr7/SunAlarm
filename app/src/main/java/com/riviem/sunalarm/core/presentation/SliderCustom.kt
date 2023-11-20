package com.riviem.sunalarm.core.presentation

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.riviem.sunalarm.ui.theme.textColor

@Composable
fun SliderCustom(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    startInterval: Int,
    endInterval: Int,
    steps: Int
) {
    Slider(
        modifier = modifier,
        value = value.toFloat(),
        onValueChange = {
            onValueChange(it.toInt())
        },
        valueRange = startInterval.toFloat()..endInterval.toFloat(),
        steps = steps,
        colors = SliderColors(
            thumbColor = textColor,
            activeTrackColor = Color(0xFF4C625F),
            activeTickColor = Color(0xFF4C625F).copy(alpha = 0.7f),
            inactiveTrackColor = Color.LightGray,
            inactiveTickColor = Color.Gray,
            disabledThumbColor = Color.LightGray,
            disabledActiveTrackColor = Color.Gray,
            disabledActiveTickColor = Color.Gray.copy(alpha = 0.7f),
            disabledInactiveTrackColor = Color.Gray,
            disabledInactiveTickColor = Color.Gray
        )
    )
}