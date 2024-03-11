package com.dev.ipati.simplecomposenavigate.core

import androidx.navigation.NavOptions

sealed class NavigateOption {
    data class Route(
        val routeName: String,
        val navOptions: NavOptions? = null
    ) : NavigateOption()

    data class DeepLink(
        val deeplink: String,
        val navOptions: NavOptions? = null
    ) : NavigateOption()
}
