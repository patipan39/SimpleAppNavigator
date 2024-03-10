package com.dev.ipati.simplecomposenavigate.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev.ipati.simplecomposenavigate.core.AppNavigator
import com.dev.ipati.simplecomposenavigate.presentation.bottombar.BottomNavigationBar
import com.dev.ipati.simplecomposenavigate.ui.theme.SimpleComposeNavigateTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel

class MainActivity : ComponentActivity() {
    private val appNavigator: AppNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleComposeNavigateTheme {
                val mainViewModel: MainViewModel = getViewModel()
                val menu = mainViewModel.showBottomMenu.collectAsState(initial = emptyList())
                val showHideBottomBar = mainViewModel.shouldShowHide.collectAsState(initial = true)
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val hasMenuOnBottom = menu.value.find { it.route == currentRoute }
                mainViewModel.setShowHideBottomBar(hasMenuOnBottom != null)
                appNavigator.setUpNavHost(navController)
                Scaffold(bottomBar = {
                    BottomNavigationBar(menu, showHideBottomBar)
                }) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) { MainNavGraph(navController = navController) }
                }
            }
        }
    }
}