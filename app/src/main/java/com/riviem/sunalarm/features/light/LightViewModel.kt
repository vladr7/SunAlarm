package com.riviem.sunalarm.features.light

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.core.data.database.asUIModel
import com.riviem.sunalarm.features.home.data.AlarmRepository
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    fun getAlarmById(createdTimestampId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val alarm = alarmRepository.getAlarmById(createdTimestampId = createdTimestampId)
            _state.update {
                it.copy(selectedAlarm = alarm.asUIModel())
            }
            println("vladlog: getAlarmById: ${alarm.asUIModel()}")
        }
    }

    fun stopAlarm(alarm: AlarmUIModel, context: Context) {
        if (alarm.isOn) {
            alarmRepository.setLightAlarm(alarm = alarm, context = context)
        }
    }
}

data class LightState(
    val selectedAlarm: AlarmUIModel? = null,
)