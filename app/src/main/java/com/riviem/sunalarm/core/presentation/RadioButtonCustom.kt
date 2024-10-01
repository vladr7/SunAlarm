package com.riviem.sunalarm.core.presentation

import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RadioButtonCustom(
    selected: Boolean,
    onSelected: () -> Unit
) {
    RadioButton(
        selected = selected,
        onClick = { onSelected() },
        colors = RadioButtonDefaults.colors(
            selectedColor = Color(0xFEEEEFFF),
            unselectedColor = Color.Gray,
            disabledSelectedColor = Color.LightGray,
        )
    )
}