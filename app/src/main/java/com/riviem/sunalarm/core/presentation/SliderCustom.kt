package com.riviem.sunalarm.core.presentation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.riviem.sunalarm.ui.theme.textColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderCustom(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderColors(
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
) {
    Slider(
        modifier = modifier,
        value = value.toFloat(),
        onValueChange = {
            onValueChange(it.toInt())
        },
        valueRange = valueRange,
        steps = steps,
        colors = colors,
        thumb = {
            CustomThumbWithLabel(
                value = value,
                interactionSource = interactionSource,
                colors = colors,
                enabled = enabled
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                colors = colors,
                enabled = enabled,
                sliderState = sliderState
            )
        },
    )
}

@Composable
fun CustomThumbWithLabel(
    value: Int,
    interactionSource: MutableInteractionSource,
    colors: SliderColors,
    enabled: Boolean
) {
    Box {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled
        )
        Text(
            text = value.toString(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-21).dp),
            fontSize = 13.sp,
            color = textColor
        )
    }
}