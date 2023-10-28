package com.riviem.sunalarm.features.home.presentation.homescreen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.core.data.database.asDatabaseModel
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
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(
        HomeState(
            selectedAlarm = AlarmUIModel(
                id = UUID.randomUUID().toString(),
                time = ZonedDateTime.now(),
                name = "Alarm",
                isOn = false,
                color = Color.Yellow
            )
        )
    )
    val state: StateFlow<HomeState> = _state

    init {
        getAlarms()
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
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.insert(alarm.asDatabaseModel())
        }
        _state.update {
            it.copy(
                showTimePickerScreen = false,
                selectedAlarm = alarm
            )
        }
    }

    fun onAddNewAlarmClick() {
        _state.update {
            it.copy(
                showTimePickerScreen = true,
                selectedAlarm = AlarmUIModel(
                    id = UUID.randomUUID().toString(),
                    time = ZonedDateTime.now(),
                    name = "Alarm",
                    isOn = false,
                    color = Color.Yellow
                )
            )
        }
    }
}

data class HomeState(
    val showTimePickerScreen: Boolean = false,
    val selectedAlarm: AlarmUIModel,
    val alarms: List<AlarmUIModel> = emptyList()
)

