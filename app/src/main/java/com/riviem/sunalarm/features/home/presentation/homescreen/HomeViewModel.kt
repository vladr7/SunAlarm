package com.riviem.sunalarm.features.home.presentation.homescreen

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.R
import com.riviem.sunalarm.core.Constants
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
                isOn = true,
                color = Color.Yellow,
                createdTimestamp = ZonedDateTime.now().toEpochSecond().toInt(),
                flashlight = false,
                days = weekDays,
                soundAlarmEnabled = false,
                minutesUntilSoundAlarm = Constants.MINUTES_UNTIL_SOUND_ALARM_INITIAL_VALUE,
                isAutoSunriseEnabled = false,
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
            if (firstDayOfWeek == FirstDayOfWeek.MONDAY.fullName) {
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
                println("vladlog: ------------------------------------")
                println("vladlog: ${databaseAlarms.asUIModel()}")
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
        if (nextAlarm == null) {
            _state.update {
                it.copy(
                    title = context.resources.getString(R.string.all_alarms_are_off),
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
        val daysString = if (days > 0) context.resources.getQuantityString(
            R.plurals.days_plural,
            days.toInt(),
            days
        ) else ""
        val hoursString = if (hours > 0) context.resources.getQuantityString(
            R.plurals.hours_plural,
            hours.toInt(),
            hours
        ) else ""
        val minutesString =
            context.resources.getQuantityString(R.plurals.minutes_plural, minutes.toInt(), minutes)
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
                showNextAlarmTimeToast = it.recentlyAddedOrCheckedAlarm,
                recentlyAddedOrCheckedAlarm = false,
            )
        }
    }

    fun onAlarmClick(alarm: AlarmUIModel) {
        println("vladlog: onAlarmClick time: ${alarm.ringTime}")
        _state.update {
            it.copy(
                showTimePickerScreen = true,
                selectedAlarm = alarm
            )
        }
        val isExpanded =
            state.value.alarms?.find { it.createdTimestamp == alarm.createdTimestamp }?.isExpandedForEdit
                ?: false
        if (isExpanded) {
            _state.update { homeState ->
                homeState.copy(
                    alarms = homeState.alarms?.map { it.copy(isExpandedForEdit = false) }
                )
            }
            return
        }
    }

    fun onSaveAlarmClick(alarm: AlarmUIModel, context: Context) {
        val nextAlarmTime = alarmRepository.getNextAlarmDateTime(alarm)
        val newAlarm = alarm.copy(ringTime = nextAlarmTime, isOn = true)
        println("vladlog: newAlarm sunrise: ${newAlarm.isAutoSunriseEnabled}")
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.insert(newAlarm.asDatabaseModel())
        }
        _state.update {
            it.copy(
                showTimePickerScreen = false,
                selectedAlarm = newAlarm,
                recentlyAddedOrCheckedAlarm = true,
                alarms = it.alarms?.map { alarmUIModel ->
                    if (alarmUIModel.createdTimestamp == newAlarm.createdTimestamp) {
                        newAlarm
                    } else {
                        alarmUIModel
                    }
                }
            )
        }
        alarmRepository.setAlarm(newAlarm, context)
    }

    fun onAddNewAlarmClick() {
        _state.update {
            it.copy(
                showTimePickerScreen = true,
                selectedAlarm = AlarmUIModel(
                    createdTimestamp = ZonedDateTime.now().toEpochSecond().toInt(),
                    ringTime = ZonedDateTime.now(),
                    name = "Alarm",
                    isOn = true,
                    color = Color.Yellow,
                    flashlight = false,
                    days = weekDays,
                    soundAlarmEnabled = false,
                    minutesUntilSoundAlarm = Constants.MINUTES_UNTIL_SOUND_ALARM_INITIAL_VALUE,
                    isAutoSunriseEnabled = false,
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
            alarmRepository.setAlarm(newAlarm, context)
            _state.update {
                it.copy(
                    recentlyAddedOrCheckedAlarm = true,
                )
            }
        } else {
            alarmRepository.cancelLightAndSoundAlarm(newAlarm.createdTimestamp, context)
        }
    }

    fun onCancelAlarmClick() {
        _state.update {
            it.copy(
                showTimePickerScreen = false
            )
        }
    }

    fun onShowNextAlarmTimeToastDone() {
        _state.update {
            it.copy(
                showNextAlarmTimeToast = false
            )
        }
    }

    fun onAlarmLongPress(alarm: AlarmUIModel) {
        val newAlarms = state.value.alarms?.map { alarmUIModel ->
            alarmUIModel.copy(isExpandedForEdit = alarmUIModel.createdTimestamp == alarm.createdTimestamp)
        }
        _state.update { homeState ->
            homeState.copy(
                selectedAlarm = alarm.copy(isExpandedForEdit = true),
                alarms = newAlarms
            )
        }
    }

    fun onDeleteAlarmClick(alarm: AlarmUIModel, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmRepository.deleteAlarm(alarm.createdTimestamp, context)
        }
    }
}

data class HomeState(
    val showTimePickerScreen: Boolean = false,
    val selectedAlarm: AlarmUIModel,
    val alarms: List<AlarmUIModel>? = null,
    val title: String,
    val subtitle: String,
    val firstDayOfWeek: FirstDayOfWeek,
    val showNextAlarmTimeToast: Boolean = false,
    val recentlyAddedOrCheckedAlarm: Boolean = false,
)

