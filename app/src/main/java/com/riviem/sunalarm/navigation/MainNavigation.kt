package com.riviem.sunalarm.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.riviem.sunalarm.ui.theme.timePickerBackgroundColor

@Composable
fun MainNavigation(
    mainAppState: MainAppState = rememberMainAppState(),
) {
    val items = getBottomNavItems()
    val navController = mainAppState.navController
    var showBottomBar by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .height(60.dp),
                    containerColor = timePickerBackgroundColor,
                ) {
                    items.forEach { destination ->
                        val selected =
                            mainAppState.currentDestination?.isTopLevelDestinationInHierarchy(
                                destination
                            ) ?: false
                        BottomNavigationItem(
                            icon = {
                                if (selected) {
                                    Icon(
                                        imageVector = destination.selectedIcon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(34.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = destination.unselectedIcon,
                                        contentDescription = null,
                                        tint = Color.LightGray,
                                        modifier = Modifier
                                            .alpha(0.7f)
                                            .blur(radius = 0.5.dp)
                                            .size(24.dp)
                                    )
                                }
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        MainNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding),
            onAlarmClick = {
                showBottomBar = false
            },
            onSaveOrDiscardClick = {
                showBottomBar = true
            }
        )
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: MainBottomDestination) =
    this?.hierarchy?.any {
        it.route?.contains(
            destination.route,
            true
        ) ?: false
    } ?: false

private fun getBottomNavItems(): List<MainBottomDestination> =
    listOf(
        MainBottomDestination.Home,
        MainBottomDestination.Settings,
    )
