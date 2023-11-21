package com.riviem.sunalarm

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.core.Constants
import com.riviem.sunalarm.features.home.data.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    fun cancelSoundAlarm(
        context: Context,
    ) {
        viewModelScope.launch {
            val alarmId = alarmRepository.getCurrentSoundAlarmIdForNotification()
            alarmRepository.cancelAlarm(
                context = context,
                alarmId = alarmId
            )
            dismissNotification(context)
        }
    }

    private fun dismissNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.DISMISS_SOUND_ALARM_NOTIFICATION_ID)
    }
}