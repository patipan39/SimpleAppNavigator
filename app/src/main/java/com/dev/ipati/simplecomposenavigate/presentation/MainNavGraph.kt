package com.dev.ipati.simplecomposenavigate.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.dev.ipati.simplecomposenavigate.core.HomeGraph
import com.dev.ipati.simplecomposenavigate.core.LoginGraph
import org.koin.androidx.compose.get

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeGraph.NameGraph.name
    ) {
        //declare nav graph
        homeGraph()
        loginGraph()
    }
}

fun NavGraphBuilder.homeGraph() {
    //Split 1 Graph
    navigation(
        HomeGraph.HomeDestination.startDestination,
        HomeGraph.NameGraph.name
    ) {
        composable(
            route = HomeGraph.HomeDestination.route,
            deepLinks = listOf(navDeepLink {
                uriPattern = HomeGraph.HomeDestination.deepLink
                action = Intent.ACTION_VIEW
            })
        ) {
            val bundle = it.savedStateHandle.get<Bundle>("message")
            bundle?.getString("test")?.let { message ->
                it.savedStateHandle.clearSavedStateProvider("message")
                Toast.makeText(get<Context>(), message, Toast.LENGTH_SHORT).show()
            }
            Home()
        }
    }
}

fun NavGraphBuilder.loginGraph() {
    //Split 1 Graph
    navigation(
        LoginGraph.LoginDestination.startDestination,
        LoginGraph.NameGraph.name
    ) {
        composable(
            route = LoginGraph.LoginDestination.route,
            deepLinks = listOf(navDeepLink {
                uriPattern = LoginGraph.LoginDestination.deepLink
                action = Intent.ACTION_VIEW
            })
        ) {
            Login()
        }
    }
}