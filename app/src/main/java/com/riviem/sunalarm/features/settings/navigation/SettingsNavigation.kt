package com.riviem.sunalarm.features.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.riviem.sunalarm.features.settings.presentation.SettingsRoute
import com.riviem.sunalarm.navigation.MainBottomDestination

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    this.navigate(MainBottomDestination.Settings.route, navOptions)
}

fun NavGraphBuilder.settingsScreen() {
    composable(route = MainBottomDestination.Settings.route) {
        SettingsRoute()
    }
}
