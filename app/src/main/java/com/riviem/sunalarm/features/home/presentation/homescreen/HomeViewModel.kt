package com.riviem.sunalarm.features.home.presentation.homescreen

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.R
import com.riviem.sunalarm.core.data.database.asDatabaseModel
import com.riviem.sunalarm.core.data.database.asUIModel
import com.riviem.sunalarm.features.home.data.AlarmRepository
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.homescreen.models.FirstDayOfWeek
import com.riviem.sunalarm.features.home.presentation.homescreen.models.weekDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val alarmRepository: AlarmRepository,
) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _state = MutableStateFlow<HomeState>(
        HomeState(
            selectedAlarm = AlarmUIModel(
                ringTime = ZonedDateTime.now(),
                name = "Alarm",
                isOn = false,
                color = Color.Yellow,
                createdTimestamp = ZonedDateTime.now().toEpochSecond().toInt(),
                flashlight = false,
                days = weekDays
            ),
            title = "",
            subtitle = "",
            firstDayOfWeek = FirstDayOfWeek.MONDAY
        )
    )
    val state: StateFlow<HomeState> = _state

    init {
        getAlarms()
        getFirstDayOfWeek()
    }

    fun getFirstDayOfWeek() {
        viewModelScope.launch {
            val firstDayOfWeek = alarmRepository.getFirstDayOfWeek()
            if(firstDayOfWeek == FirstDayOfWeek.MONDAY.fullName) {
                _state.update {
                    it.copy(
                        firstDayOfWeek = FirstDayOfWeek.MONDAY
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        firstDayOfWeek = FirstDayOfWeek.SUNDAY
                    )
                }
            }
        }
    }

    private fun getAlarms() {
        viewModelScope.launch {
            alarmRepository.getAlarms().collectLatest { databaseAlarms ->
//                println("vladlog: ------------------------------------")
//                println("vladlog: ${databaseAlarms.asUIModel()}")
                _state.update {
                    it.copy(alarms = databaseAlarms.asUIModel())
                }
                updateTitleAndSubtitle(databaseAlarms.asUIModel())
            }
        }
    }

    private fun updateTitleAndSubtitle(alarms: List<AlarmUIModel>) {
        val activeAlarms = alarms.filter { it.isOn }
        val nextAlarm = activeAlarms.minByOrNull { it.ringTime }
        if(nextAlarm == null) {
            _state.update {
                it.copy(
                    title = context.resources.getString(R.string.no_alarms),
                    subtitle = "",
                )
            }
            return
        }
        val nextAlarmTime = alarmRepository.getNextAlarmDateTime(nextAlarm)
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d, HH:mm")
        val subtitle = nextAlarmTime.format(formatter)
        val duration = Duration.between(ZonedDateTime.now(), nextAlarmTime)
        val days = duration.toDays()
        val hours = duration.minusDays(days).toHours()
        val minutes = duration.minusDays(days).minusHours(hours).toMinutes()
        val daysString = if (days > 0) context.resources.getQuantityString(R.plurals.days_plural, days.toInt(), days) else ""
        val hoursString = if (hours > 0) context.resources.getQuantityString(R.plurals.hours_plural, hours.toInt(), hours) else ""
        val minutesString = context.resources.getQuantityString(R.plurals.minutes_plural, minutes.toInt(), minutes)
        val titleBuilder = StringBuilder(context.resources.getString(R.string.next_alarm_in))
        if (days > 0) {
            titleBuilder.append("$days $daysString ")
        }
        if (hours > 0) {
            titleBuilder.append("$hours $hoursString ")
        }
        titleBuilder.append("$minutes $minutesString")
        val title = titleBuilder.toString().trim()
        _state.update {
            it.copy(
                subtitle = subtitle,
                title = title,
            )
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

    fun onSaveAlarmClick(alarm: AlarmUIModel, context: Context) {
        val nextAlarmTime = alarmRepository.getNextAlarmDateTime(alarm)
        val newAlarm = alarm.copy(ringTime = nextAlarmTime)
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.insert(newAlarm.asDatabaseModel())
        }
        _state.update {
            it.copy(
                showTimePickerScreen = false,
                selectedAlarm = newAlarm
            )
        }
        if (alarm.isOn) {
            alarmRepository.setLightAlarm(newAlarm, context)
        }
    }

    fun onAddNewAlarmClick() {
        _state.update {
            it.copy(
                showTimePickerScreen = true,
                selectedAlarm = AlarmUIModel(
                    createdTimestamp = ZonedDateTime.now().toEpochSecond().toInt(),
                    ringTime = ZonedDateTime.now(),
                    name = "Alarm",
                    isOn = false,
                    color = Color.Yellow,
                    flashlight = false,
                    days = weekDays
                )
            )
        }
    }

    fun onAlarmCheckChanged(checked: Boolean, alarm: AlarmUIModel, context: Context) {
        val nextAlarmTime = alarmRepository.getNextAlarmDateTime(alarm)
        val newAlarm = alarm.copy(isOn = checked, ringTime = nextAlarmTime)
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.insert(newAlarm.asDatabaseModel())
        }
        if (checked) {
            alarmRepository.setLightAlarm(newAlarm, context)
        } else {
            alarmRepository.cancelAlarm(newAlarm, context)
        }
    }

    fun onCancelAlarmClick() {
        _state.update {
            it.copy(
                showTimePickerScreen = false
            )
        }
    }
}

data class HomeState(
    val showTimePickerScreen: Boolean = false,
    val selectedAlarm: AlarmUIModel,
    val alarms: List<AlarmUIModel> = emptyList(),
    val title: String,
    val subtitle: String,
    val firstDayOfWeek: FirstDayOfWeek
)

