package com.riviem.sunalarm.features.home.presentation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    HomeScreen(
        context = context
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context
) {

    var hour by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {

                },
            ) {
                Text("Set Alarm")
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextField(
                    value = hour.toString(),
                    onValueChange = { hour = it.toInt() },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                )
                TextField(
                    value = minute.toString(),
                    onValueChange = { minute = it.toInt() },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }
    }
}

