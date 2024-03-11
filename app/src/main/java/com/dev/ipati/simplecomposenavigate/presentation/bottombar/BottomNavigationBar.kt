package com.dev.ipati.simplecomposenavigate.presentation.bottombar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.dev.ipati.simplecomposenavigate.core.AppNavigator
import com.dev.ipati.simplecomposenavigate.core.HomeGraph
import com.dev.ipati.simplecomposenavigate.core.NavigateOption
import com.dev.ipati.simplecomposenavigate.presentation.profile.ProfileMiddleWare
import org.koin.androidx.compose.get

@Composable
fun BottomNavigationBar(menu: State<List<BottomNavItem>>?, showHideBottomBar: State<Boolean>) {
    val navController = get<AppNavigator>()
    val profileMiddleWare = get<ProfileMiddleWare>()
    AnimatedVisibility(
        visible = showHideBottomBar.value,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }) {
        BottomNavigation {
            val navBackStackEntry by navController.getNavHost().currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            menu?.value?.forEach { item ->
                BottomNavigationItem(
                    selected = item.route == currentRoute,
                    onClick = {
                        val middleWare = when (item.route) {
                            HomeGraph.ProfileDestination.route -> {
                                profileMiddleWare
                            }

                            else -> null
                        }
                        navController.push(
                            navigate = NavigateOption.Route(item.route, navOptions {
                                popUpTo(navController.getNavHost().graph.startDestinationId) {
                                    saveState = true
                                }
                                restoreState = true
                                launchSingleTop = true
                            }),
                            middleWare = middleWare
                        )
                    },
                    icon = { Icon(imageVector = item.icon, contentDescription = null) },
                    label = { Text(text = item.label) }
                )
            }
        }
    }
}