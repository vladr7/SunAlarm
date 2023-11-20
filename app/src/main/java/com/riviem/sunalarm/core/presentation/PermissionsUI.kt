package com.riviem.sunalarm.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.riviem.sunalarm.R
import com.riviem.sunalarm.ui.theme.alarmColor
import com.riviem.sunalarm.ui.theme.textColor

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NormalPermissionDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onConfirmClicked: () -> Unit,
) {
    AlertDialog(
        modifier = modifier
            .background(
                color = alarmColor,
                shape = RoundedCornerShape(8.dp)
            ),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
        ),
        content = {
            Column(
                modifier = modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(bottom = 20.dp, start = 15.dp, end = 15.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    color = textColor
                )
                Text(
                    text = description,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .padding(bottom = 20.dp, start = 15.dp, end = 15.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    color = textColor
                )
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = textColor,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(end = 25.dp)
                            .clickable { onDismissRequest() }
                    )
                    Text(
                        text = stringResource(R.string.ok), color = textColor, fontSize = 20.sp,
                        modifier = Modifier
                            .padding(end = 3.dp)
                            .clickable { onConfirmClicked() }
                    )
                }
            }
        }
    )
}