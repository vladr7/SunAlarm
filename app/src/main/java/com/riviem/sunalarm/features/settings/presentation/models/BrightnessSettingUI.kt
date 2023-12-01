package com.riviem.sunalarm.features.settings.presentation.models

import com.riviem.sunalarm.core.Constants

data class BrightnessSettingUI(
    val brightness: Int = 0,
    val brightnessGraduallyMinutes: Int = Constants.DEFAULT_BRIGHTNESS_GRADUAL_VALUE
)
