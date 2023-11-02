package com.riviem.sunalarm.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.features.home.data.AlarmRepository
import com.riviem.sunalarm.features.home.presentation.homescreen.models.FirstDayOfWeek
import com.riviem.sunalarm.features.settings.presentation.models.BrightnessSettingUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsViewState>(SettingsViewState())
    val state: StateFlow<SettingsViewState> = _state

    init {
        getSnoozeLength()
        viewModelScope.launch {
            getBrightnessSettings()
        }
        acquireFirstDayOfWeek()
    }

    private fun acquireFirstDayOfWeek() {
        viewModelScope.launch {
            val firstDayOfWeek = alarmRepository.getFirstDayOfWeek()
            _state.update {
                it.copy(
                    firstDayOfWeek = FirstDayOfWeek.getFirstDayOfWeek(firstDayOfWeek)
                )
            }
        }
    }

    private suspend fun getBrightnessSettings() {
        val brightnessSettings = alarmRepository.getBrightnessSettings()
        _state.update {
            it.copy(
                brightnessSettingUI = brightnessSettings
            )
        }
    }

    private fun getSnoozeLength() {
        viewModelScope.launch {
            val snoozeLength = alarmRepository.getSnoozeLength()
            _state.update {
                it.copy(snoozeLength = snoozeLength)
            }
        }
    }

    fun setSnoozeLength() {
        viewModelScope.launch {
            alarmRepository.setSnoozeLength(state.value.snoozeLength)
        }
    }

    fun setBrightnessSettings(brightnessSettingUI: BrightnessSettingUI) {
        viewModelScope.launch {
            alarmRepository.setBrightnessSettings(brightnessSettingUI = brightnessSettingUI)
            getBrightnessSettings()
        }
    }

    fun setFirstDayOfWeek(firstDayOfWeek: FirstDayOfWeek) {
        viewModelScope.launch {
            alarmRepository.setFirstDayOfWeek(firstDayOfWeek.fullName)
            acquireFirstDayOfWeek()
        }
    }

    fun setMinutesUntilSoundAlarm(minutes: Int) {
        viewModelScope.launch {
            alarmRepository.setMinutesUntilSoundAlarm(minutes)
        }
    }
}


data class SettingsViewState(
    val snoozeLength: Int = 0,
    val brightnessSettingUI: BrightnessSettingUI = BrightnessSettingUI(),
    val firstDayOfWeek: FirstDayOfWeek = FirstDayOfWeek.MONDAY
)