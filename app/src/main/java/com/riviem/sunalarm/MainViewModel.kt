package com.riviem.sunalarm

import android.content.Context
import androidx.lifecycle.ViewModel
import com.riviem.sunalarm.features.home.data.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
): ViewModel() {

    fun cancelSoundAlarm(
        context: Context,
        soundAlarmId: Int
    ) {
        alarmRepository.cancelSoundAlarm(
            context = context,
            soundAlarmId = soundAlarmId
        )
    }
}