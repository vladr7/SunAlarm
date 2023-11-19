package com.riviem.sunalarm.core.presentation

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SwitchCustom(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = {
            onCheckedChange(it)
        },
        colors = SwitchColors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color(0xFF4C625F),
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color.Gray,
            checkedBorderColor = Color.Transparent,
            uncheckedBorderColor = Color.Transparent,
            checkedIconColor = Color.Transparent,
            uncheckedIconColor = Color.Transparent,
            disabledCheckedThumbColor = Color.LightGray,
            disabledCheckedTrackColor = Color.Gray,
            disabledUncheckedThumbColor = Color.LightGray,
            disabledUncheckedTrackColor = Color.Gray,
            disabledCheckedBorderColor = Color.Gray,
            disabledUncheckedBorderColor = Color.Gray,
            disabledCheckedIconColor = Color.Gray,
            disabledUncheckedIconColor = Color.Gray
        )
    )
}