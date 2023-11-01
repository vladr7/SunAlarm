package com.riviem.sunalarm.features.light

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.core.data.database.asUIModel
import com.riviem.sunalarm.features.home.data.AlarmRepository
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.settings.presentation.models.BrightnessSettingUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LightViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LightState>(LightState())
    val state: StateFlow<LightState> = _state

    init {
        getBrightnessSettings()
    }

    private fun getBrightnessSettings() {
        viewModelScope.launch {
            val brightnessSettings = alarmRepository.getBrightnessSettings()
            _state.update {
                it.copy(
                    brightnessSettingUI = brightnessSettings
                )
            }
            increaseBrightnessOvertime(brightnessSettings)
        }
    }

    private fun increaseBrightnessOvertime(brightnessSettingUI: BrightnessSettingUI) {
        viewModelScope.launch {
            var currentBrightness = brightnessSettingUI.brightness
            val brightnessIncrement = 1  // Adjust as necessary
            val totalIterations = (255 - currentBrightness) / brightnessIncrement
            val totalDurationMillis = brightnessSettingUI.brightnessGraduallyMinutes * 60L * 1000L
            val delayPerIteration = totalDurationMillis / totalIterations
            for (i in 1..totalIterations) {
                delay(delayPerIteration)  // Dynamic delay
                currentBrightness += brightnessIncrement
                if (currentBrightness > 255) {
                    currentBrightness = 255
                }
                _state.update {
                    it.copy(
                        brightnessSettingUI = it.brightnessSettingUI.copy(
                            brightness = currentBrightness
                        )
                    )
                }
            }
        }
    }



    fun getAlarmById(createdTimestampId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val alarm = alarmRepository.getAlarmById(createdTimestampId = createdTimestampId)
            println("vladlog: getAlarmById: $alarm")
            _state.update {
                it.copy(selectedAlarm = alarm.asUIModel())
            }
        }
    }

    fun stopAlarm(alarm: AlarmUIModel, context: Context) {
        if (alarm.isOn) {
            alarmRepository.setLightAlarm(alarm = alarm, context = context)
        }
    }

    fun snoozeAlarm(alarm: AlarmUIModel, context: Context) {
        if (alarm.isOn) {
            viewModelScope.launch {
                alarmRepository.snoozeAlarm(alarm = alarm, context = context)
            }
        }
    }
}

data class LightState(
    val selectedAlarm: AlarmUIModel? = null,
    val brightnessSettingUI: BrightnessSettingUI = BrightnessSettingUI(),
)