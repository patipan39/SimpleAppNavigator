package com.dev.ipati.simplecomposenavigate.core

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import org.koin.android.BuildConfig

interface AppNavigator {
    fun setUpNavHost(navHost: NavHostController): NavHostController?
    fun push(navigate: NavigateOption)
    fun pop()
    fun popWithResult(key: String, bundle: Bundle)
    fun popAll(isPopInclusive: Boolean = false)
    fun refresh()
    fun popUntil(navigate: NavigateOption, isPopInclusive: Boolean = false)
    fun hasDestinationIdInStack(destinationId: Int): Boolean
    fun hasDeepLinkInStack(url: String): Boolean
    fun hasRouteInStack(route: String): Boolean
    fun getRootDestinationId(): Int
}

@SuppressLint("RestrictedApi")
class AppNavigatorImpl : AppNavigator {
    private var navHost: NavHostController? = null

    override fun setUpNavHost(navHost: NavHostController): NavHostController? {
        this.navHost = navHost
        return this.navHost
    }

    override fun push(navigate: NavigateOption) {
        when (navigate) {
            is NavigateOption.Route -> {
                navHost?.navigate(route = navigate.routeName)
            }

            is NavigateOption.DeepLink -> {
                navHost?.navigate(deepLink = Uri.parse(navigate.deeplink))
            }
        }
    }

    override fun pop() {
        navHost?.popBackStack()
    }

    override fun popWithResult(key: String, bundle: Bundle) {
        navHost?.previousBackStackEntry?.savedStateHandle?.set(key, bundle)
        navHost?.popBackStack()
    }

    override fun popAll(isPopInclusive: Boolean) {
        try {
            val destinationId = navHost?.currentBackStack?.value?.firstNotNullOfOrNull {
                it.destination.takeIf { destination -> destination !is NavGraph }
            }?.id!!
            navHost?.popBackStack(destinationId, isPopInclusive)
        } catch (e: Exception) {
            toast(e)
        }
    }

    override fun refresh() {
        try {
            val id = navHost?.currentDestination!!.id
            navHost?.popBackStack(id, true)
            navHost?.navigate(id)
        } catch (e: Exception) {
            toast(e)
        }
    }

    override fun popUntil(navigate: NavigateOption, isPopInclusive: Boolean) {
        when (navigate) {
            is NavigateOption.Route -> {
                navHost?.popBackStack(navigate.routeName, isPopInclusive)
            }

            is NavigateOption.DeepLink -> {
                try {
                    val destinationId = navHost?.currentBackStack?.value
                        ?.mapNotNull { it.destination.takeIf { it !is NavGraph } }
                        ?.firstOrNull {
                            it.hasDeepLink(Uri.parse(navigate.deeplink))
                        }
                        ?.id!!
                    navHost?.popBackStack(destinationId, isPopInclusive)
                } catch (e: Exception) {
                    toast(e)
                }
            }
        }
    }

    override fun hasDestinationIdInStack(destinationId: Int): Boolean {
        return try {
            navHost?.currentBackStack?.value
                ?.mapNotNull { it.destination.takeIf { it !is NavGraph } }
                ?.any { it.id == destinationId } ?: false
        } catch (e: Exception) {
            toast(e)
            false
        }
    }

    override fun hasDeepLinkInStack(url: String): Boolean {
        return try {
            navHost?.currentBackStack?.value?.mapNotNull { it.destination.takeIf { it !is NavGraph } }
                ?.any { it.hasDeepLink(Uri.parse(url)) } ?: false
        } catch (e: Exception) {
            toast(e)
            false
        }
    }

    override fun hasRouteInStack(route: String): Boolean {
        return try {
            navHost?.currentBackStack?.value?.mapNotNull { it.destination.takeIf { it !is NavGraph } }
                ?.any { it.route == route } ?: false
        } catch (e: Exception) {
            toast(e)
            false
        }
    }

    override fun getRootDestinationId(): Int {
        var rootDestinationId: Int? = null
        try {
            rootDestinationId = navHost?.currentBackStack?.value?.firstNotNullOfOrNull {
                it.destination.takeIf { destination -> destination !is NavGraph }
            }?.id
        } catch (e: Exception) {
            toast(e)
        }
        return rootDestinationId ?: -1
    }

    private fun toast(e: Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
            navHost?.context?.let {
                var message = e.message ?: "Unknown"
                val notFoundDestination = "Cannot be found from the current destination"
                val notFoundGraph = "Cannot be found in the Navigation graph"
                message = when {
                    message.contains(notFoundDestination) -> notFoundDestination
                    message.contains(notFoundGraph) -> notFoundGraph
                    else -> message
                }
                Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}