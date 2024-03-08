package com.dev.ipati.simplecomposenavigate.core

sealed class NavigateOption {
    data class Route(val routeName: String) : NavigateOption()
    data class DeepLink(val deeplink: String) : NavigateOption()
}
