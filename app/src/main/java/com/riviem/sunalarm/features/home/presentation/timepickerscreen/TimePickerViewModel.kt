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
            getSunriseTime(activity)
        }
    }

    suspend fun getSunriseTime(activity: Activity) {
        val coordinates = getCoordinates(activity)
        if (coordinates != null) {
            try {
                val response =
                    RetrofitInstance.sunriseApiService.getSunriseTime(
                        coordinates.latitude,
                        -coordinates.longitude
                    ).await()
                _state.update {
                    it.copy(
                        sunriseTime = extractHourAndMinute(response.results.sunrise)
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        showInternetOffError = true
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    showGeneralError = true
                )
            }
        }
    }

    fun resetToasts() {
        _state.update {
            it.copy(
                showInternetOffError = null,
                showGeneralError = null
            )
        }
    }

}

data class TimePickerState(
    val sunriseTime: HourMinute? = null,
    val showInternetOffError: Boolean? = null,
    val showGeneralError: Boolean? = null,
)