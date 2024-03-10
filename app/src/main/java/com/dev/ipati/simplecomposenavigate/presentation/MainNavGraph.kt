package com.dev.ipati.simplecomposenavigate.presentation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.dev.ipati.simplecomposenavigate.core.HomeGraph
import com.dev.ipati.simplecomposenavigate.core.LoginGraph
import com.dev.ipati.simplecomposenavigate.presentation.home.Home
import com.dev.ipati.simplecomposenavigate.presentation.login.Login
import com.dev.ipati.simplecomposenavigate.presentation.profile.Profile
import com.dev.ipati.simplecomposenavigate.presentation.search.Search

@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeGraph.HomeDestination.route
    ) {
        bottomNavigationGraph()
        loginGraph()
    }
}

fun NavGraphBuilder.bottomNavigationGraph() {
    //split 1 Graph
    composable(
        route = HomeGraph.HomeDestination.route,
        deepLinks = listOf(navDeepLink {
            uriPattern = HomeGraph.HomeDestination.deepLink
            action = Intent.ACTION_VIEW
        })
    ) {
        Home()
    }
    composable(
        route = HomeGraph.SearchDestination.route,
        deepLinks = listOf(navDeepLink {
            uriPattern = HomeGraph.SearchDestination.deepLink
            action = Intent.ACTION_VIEW
        })
    ) {
        Search()
    }

    composable(route = HomeGraph.ProfileDestination.route, deepLinks = listOf(navDeepLink {
        uriPattern = HomeGraph.ProfileDestination.deepLink
        action = Intent.ACTION_VIEW
    })) {
        Profile()
    }
}

fun NavGraphBuilder.loginGraph() {
    //nested 1 graph
    navigation(
        LoginGraph.LoginDestination.route,
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