package com.riviem.sunalarm.features.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.riviem.sunalarm.features.home.presentation.homescreen.HomeRoute
import com.riviem.sunalarm.navigation.MainBottomDestination

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(MainBottomDestination.Home.route, navOptions)
}

fun NavGraphBuilder.homeScreen() {
    composable(route = MainBottomDestination.Home.route) {
        HomeRoute(

        )
    }
}
