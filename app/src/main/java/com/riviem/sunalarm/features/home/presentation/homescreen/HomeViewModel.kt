package com.riviem.sunalarm.features.home.presentation.homescreen

import androidx.lifecycle.ViewModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        getAlarms()
    }

    private fun getAlarms() {

    }

    fun onAlarmClick(alarm: AlarmUIModel) {
        _state.update {
            it.copy(
                showTimePickerScreen = true,
                selectedAlarm = alarm
            )
        }
    }

    fun onSaveAlarmClick(alarm: AlarmUIModel) {
        _state.update {
            it.copy(
                showTimePickerScreen = false,
                selectedAlarm = alarm
            )
        }
    }
}

data class HomeState(
    val showTimePickerScreen: Boolean = false,
    val selectedAlarm: AlarmUIModel? = null,
    val alarms: List<AlarmUIModel> = emptyList()
)

