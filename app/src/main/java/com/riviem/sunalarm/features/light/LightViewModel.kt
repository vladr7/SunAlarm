package com.riviem.sunalarm.features.light

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.core.data.api.sunrise.RetrofitInstance
import com.riviem.sunalarm.core.data.database.DatabaseAlarm
import com.riviem.sunalarm.core.data.database.asUIModel
import com.riviem.sunalarm.core.presentation.enums.AlarmType
import com.riviem.sunalarm.core.presentation.extractHourAndMinute
import com.riviem.sunalarm.core.presentation.getCoordinates
import com.riviem.sunalarm.core.presentation.hasNotificationPermission
import com.riviem.sunalarm.features.home.data.AlarmRepository
import com.riviem.sunalarm.features.home.data.WhiteNoiseRepository
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.models.HourMinute
import com.riviem.sunalarm.features.settings.presentation.models.BrightnessSettingUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.await
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class LightViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val whiteNoiseRepository: WhiteNoiseRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LightState>(LightState())
    val state: StateFlow<LightState> = _state

    init {
        getBrightnessSettings()
        setCurrentTime()
        getSnoozeTime()
    }

    private fun getSnoozeTime() {
        viewModelScope.launch {
            val snoozeTime = alarmRepository.getSnoozeLength()
            _state.update {
                it.copy(snoozeLength = snoozeTime)
            }
        }
    }

    private fun setCurrentTime() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val now = ZonedDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val currentTime = now.format(formatter)
                _state.update {
                    it.copy(
                        currentTime = currentTime
                    )
                }
                delay(1000)
            }
        }
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
            val totalIterations = 100 - currentBrightness
            val totalDurationMillis = brightnessSettingUI.brightnessGraduallyMinutes * 60L * 1000L
            val delayPerIteration = 2 * totalDurationMillis / totalIterations

            for (i in 1..totalIterations) {
                delay(delayPerIteration)
                currentBrightness++
                if (currentBrightness > 100) {
                    currentBrightness = 100
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


    fun getAlarmById(createdTimestampId: Int, alarmType: AlarmType, context: Context) {
        if (createdTimestampId == -1) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val id = if (alarmType == AlarmType.LIGHT) {
                createdTimestampId
            } else {
                createdTimestampId - 1
            }
            val alarm = alarmRepository.getAlarmById(createdTimestampId = id)
            _state.update {
                it.copy(selectedAlarm = alarm.asUIModel())
            }
            checkShouldCreateDimissSoundAlarmNotification(alarm, alarmType, context = context)
            initShowDismissSoundButton(alarmType)
        }
    }

    private fun checkShouldCreateDimissSoundAlarmNotification(
        alarm: DatabaseAlarm,
        alarmType: AlarmType,
        context: Context
    ) {
        if (alarm.soundEnabled && alarmType == AlarmType.LIGHT) {
            if(hasNotificationPermission(context = context)) {
                _state.update {
                    it.copy(shouldCreateDismissSoundNotification = true)
                }
            }
        }
    }

    private fun initShowDismissSoundButton(alarmType: AlarmType) {
        val showDismissSoundButton = if (alarmType == AlarmType.SOUND) {
            false
        } else {
            state.value.selectedAlarm?.soundAlarmEnabled == true
        }
        viewModelScope.launch {
            _state.update {
                it.copy(showDismissSoundButton = showDismissSoundButton)
            }
        }
        if (showDismissSoundButton) {
            viewModelScope.launch {
                alarmRepository.setCurrentSoundAlarmIdForNotification(
                    soundAlarmId = (state.value.selectedAlarm?.createdTimestamp?.plus(1)) ?: -1
                )
            }
        }
    }

    suspend fun setNextLightAlarm(
        alarm: AlarmUIModel,
        context: Context,
        alarmType: AlarmType,
        activity: Activity
    ) {
        if (alarm.isOn && (!alarm.soundAlarmEnabled || alarmType == AlarmType.SOUND)) {
            if (!alarm.isAutoSunriseEnabled) {
                alarmRepository.setAlarm(alarm = alarm, context = context)
            } else {
                val sunriseTime =
                    getSunriseTime(activity) ?: alarmRepository.getSunriseTime()
                alarmRepository.setAlarm(
                    alarm = alarm.copy(
                        ringTime = ZonedDateTime.now().withHour(sunriseTime.hour)
                            .withMinute(sunriseTime.minute)
                    ), context = context
                )
            }
        }
        _state.update {
            it.copy(
                selectedAlarm = alarm.copy(flashlight = false),
            )
        }
    }

    private suspend fun getSunriseTime(activity: Activity): HourMinute? {
        return try {
            val coordinates = getCoordinates(activity)
            return if (coordinates != null) {
                try {
                    val response =
                        RetrofitInstance.sunriseApiService.getSunriseTime(
                            coordinates.latitude,
                            -coordinates.longitude
                        ).await()
                    val sunriseTime = extractHourAndMinute(response.results.sunrise)
                    sunriseTime
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun stopSoundAlarm(alarm: AlarmUIModel, context: Context) {
        alarmRepository.cancelAlarm(alarmId = alarm.createdTimestamp + 1, context = context)
        _state.update {
            it.copy(
                selectedAlarm = alarm.copy(flashlight = false),
                showDismissSoundButton = false
            )
        }
    }

    fun snoozeAlarm(alarm: AlarmUIModel, context: Context, alarmType: AlarmType) {
        if (alarm.isOn) {
            viewModelScope.launch {
                alarmRepository.snoozeAlarm(alarm = alarm, context = context, alarmType = alarmType)
            }
        }
        _state.update {
            it.copy(
                selectedAlarm = alarm.copy(flashlight = false),
            )
        }
    }

    fun playStopWhiteNoise() {
        viewModelScope.launch {
            val volume = alarmRepository.getWhiteNoiseVolume()
            whiteNoiseRepository.playOrStopWhiteNoise(volume)
        }
    }
}

data class LightState(
    val selectedAlarm: AlarmUIModel? = null,
    val brightnessSettingUI: BrightnessSettingUI = BrightnessSettingUI(),
    val currentTime: String = "",
    val snoozeLength: Int = 0,
    val showDismissSoundButton: Boolean = false,
    val shouldCreateDismissSoundNotification: Boolean = false,
)