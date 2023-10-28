package com.riviem.sunalarm.features.home.presentation.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.core.data.database.DatabaseAlarm
import com.riviem.sunalarm.core.data.database.asUIModel
import com.riviem.sunalarm.features.home.data.AlarmRepository
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        updateAlarmList()
        getAlarms()
    }

    private fun updateAlarmList() {
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.insertAll(
                alarms = listOf(
                    DatabaseAlarm(
                        id = 1,
                        time = "12:00",
                        name = "Alarm 1",
                        isOn = true,
                        days = listOf()
                    ),
                    DatabaseAlarm(
                        id = 2,
                        time = "12:05",
                        name = "Alarm 2",
                        isOn = true,
                        days = listOf()
                    ),
                )
            )
        }
    }

    private fun getAlarms() {
        viewModelScope.launch {
            alarmRepository.getAlarms().collectLatest { databaseAlarms ->
                _state.update {
                    it.copy(alarms = databaseAlarms.asUIModel())
                }
            }
        }
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

