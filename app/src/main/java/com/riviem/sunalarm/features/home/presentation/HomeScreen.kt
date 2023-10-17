package com.riviem.sunalarm.features.home.presentation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.riviem.sunalarm.MainActivity


@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    HomeScreen(
        context = context,
    )
}

@SuppressLint("ScheduleExactAlarm")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
) {
    val activity = context as MainActivity

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeScreenTitle(
                modifier = modifier
                    .padding(top = 100.dp),
            )
        }
    }
}

@Composable
fun HomeScreenTitle(
    modifier: Modifier = Modifier,
    title: String = "Next alarm in 6 hours 1 minute",
    subtitle: String = "Tue, Oct 17, 15:29",
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 30.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

