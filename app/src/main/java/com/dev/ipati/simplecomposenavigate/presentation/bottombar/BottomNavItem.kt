package com.dev.ipati.simplecomposenavigate.presentation.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.dev.ipati.simplecomposenavigate.core.HomeGraph

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem(HomeGraph.HomeDestination.route, Icons.Default.Home, "Home")
    data object Search :
        BottomNavItem(HomeGraph.SearchDestination.route, Icons.Default.Search, "Search")

    data object Profile :
        BottomNavItem(HomeGraph.ProfileDestination.route, Icons.Default.Person, "Profile")
}