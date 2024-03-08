package com.dev.ipati.simplecomposenavigate.core

object Destinations {
    const val Home = "home"
    const val Login = "home/login"
}

sealed class HomeGraph {
    object NameGraph {
        const val name = "HomeGraph"
    }

    object HomeDestination {
        const val startDestination = Destinations.Home
        const val route: String = Destinations.Home
        const val deepLink: String = "example://compose/home"
    }
}

sealed class LoginGraph {
    object NameGraph {
        const val name = "LoginGraph"
    }

    object LoginDestination {
        const val startDestination = Destinations.Login
        const val route: String = Destinations.Login
        const val deepLink: String = "example://compose/login"
    }
}