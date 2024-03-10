package com.dev.ipati.simplecomposenavigate.core

object Destinations {
    const val Home = "home"
    const val Search = "search"
    const val Profile = "profile"
    const val Login = "home/login"
}

sealed class HomeGraph {
    object NameGraph {
        const val name = "HomeGraph"
    }

    data object HomeDestination {
        const val route: String = Destinations.Home
        const val deepLink: String = "example://compose/home"
    }

    data object SearchDestination {
        const val route: String = Destinations.Search
        const val deepLink: String = "example://compose/search"
    }

    data object ProfileDestination {
        const val route: String = Destinations.Profile
        const val deepLink: String = "example://compose/profile"
    }
}

sealed class LoginGraph {
    object NameGraph {
        const val name = "LoginGraph"
    }

    object LoginDestination {
        const val route: String = Destinations.Login
        const val deepLink: String = "example://compose/login"
    }
}