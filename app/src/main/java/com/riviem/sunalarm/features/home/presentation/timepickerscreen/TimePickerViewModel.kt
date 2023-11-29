package com.riviem.sunalarm.features.home.presentation.timepickerscreen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riviem.sunalarm.core.data.api.sunrise.RetrofitInstance
import com.riviem.sunalarm.core.presentation.extractHourAndMinute
import com.riviem.sunalarm.core.presentation.getCoordinates
import com.riviem.sunalarm.features.home.data.AlarmRepository
import com.riviem.sunalarm.features.home.presentation.homescreen.models.AlarmUIModel
import com.riviem.sunalarm.features.home.presentation.timepickerscreen.models.HourMinute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.await
import javax.inject.Inject

@HiltViewModel
class TimePickerViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _state = MutableStateFlow<TimePickerState>(
        TimePickerState()
    )
    val state: StateFlow<TimePickerState> = _state

    fun initSunriseTime(alarm: AlarmUIModel, activity: Activity) {
        if (!alarm.isAutoSunriseEnabled) {
            return
        }
        viewModelScope.launch {
            val sunriseTime = getSunriseTime(activity)
            if (sunriseTime != null) {
                alarmRepository.setSunriseTime(sunriseTime = sunriseTime)
            }
        }
    }

    suspend fun getSunriseTime(activity: Activity): HourMinute? {
        val coordinates = getCoordinates(activity)
        return if (coordinates != null) {
            try {
                val response =
                    RetrofitInstance.sunriseApiService.getSunriseTime(
                        coordinates.latitude,
                        -coordinates.longitude
                    ).await()
                val sunriseTime = extractHourAndMinute(response.results.sunrise)
                _state.update {
                    it.copy(
                        sunriseTime = sunriseTime
                    )
                }
                sunriseTime
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        showInternetOffError = true
                    )
                }
                null
            }
        } else {
            _state.update {
                it.copy(
                    showGeneralError = true
                )
            }
            null
        }
    }

    fun resetToasts() {
        _state.update {
            it.copy(
                showInternetOffError = null,
                showGeneralError = null,
                showSunriseFeatureDisabledToast = false,
                showSunriseFeatureEnabledToast = false,
            )
        }
    }

    fun showSunriseFeatureEnabledToast() {
        _state.update {
            it.copy(
                showSunriseFeatureEnabledToast = true
            )
        }
    }

    fun showSunriseFeatureDisabledToast() {
        _state.update {
            it.copy(
                showSunriseFeatureDisabledToast = true
            )
        }
    }
}

data class TimePickerState(
    val sunriseTime: HourMinute? = null,
    val showInternetOffError: Boolean? = null,
    val showGeneralError: Boolean? = null,
    val showSunriseFeatureEnabledToast: Boolean = false,
    val showSunriseFeatureDisabledToast: Boolean = false,
)