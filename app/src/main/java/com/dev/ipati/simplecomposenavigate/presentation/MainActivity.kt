package com.dev.ipati.simplecomposenavigate.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.dev.ipati.simplecomposenavigate.core.AppNavigator
import com.dev.ipati.simplecomposenavigate.ui.theme.SimpleComposeNavigateTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val appNavigator: AppNavigator by inject()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleComposeNavigateTheme {
                val navController = rememberNavController()
                MainNavGraph(navController)
                appNavigator.setUpNavHost(navController)
            }
        }
    }
}