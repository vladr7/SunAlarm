package com.riviem.sunalarm.features.light

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LightScreen() {
    val activity = androidx.compose.ui.platform.LocalContext.current as android.app.Activity
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.Yellow.copy(alpha = 0.8f)
            ),
        verticalArrangement = SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                activity.finishAffinity()
            },
            modifier = Modifier.size(200.dp),
            shape = RoundedCornerShape(5)
        ) {
            Text(
                text = "Snooze",
                fontSize = 24.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        Button(
            onClick = {
                activity.finishAffinity()
            },
            modifier = Modifier.size(200.dp),
            shape = RoundedCornerShape(5)
        ) {
            Text(
                text = "Stop",
                fontSize = 24.sp
            )
        }
    }
}