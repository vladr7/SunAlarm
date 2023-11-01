package com.riviem.sunalarm.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.features.home.data.AlarmRepository
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
    }

    private fun getSnoozeLength() {
        viewModelScope.launch {
            val snoozeLength = alarmRepository.getSnoozeLength()
            _state.update {
                it.copy(snoozeLength = snoozeLength)
            }
        }
    }

    fun setSnoozeLength(length: Int) {
        viewModelScope.launch {
            alarmRepository.setSnoozeLength(snoozeLength = length)
        }
        _state.update {
            it.copy(snoozeLength = length)
        }
    }

    fun setBrightnessSettings(brightnessSettingUI: BrightnessSettingUI) {
        viewModelScope.launch {
            alarmRepository.setBrightnessSettings(brightnessSettingUI = brightnessSettingUI)
        }
    }
}


data class SettingsViewState(
    val snoozeLength: Int = 0
)