package com.dev.ipati.simplecomposenavigate.core

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dev.ipati.simplecomposenavigate.presentation.middleware.MiddleWare
import com.dev.ipati.simplecomposenavigate.presentation.middleware.MiddleWareManager
import org.koin.android.BuildConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface AppNavigator {
    fun setUpNavHost(navHost: NavHostController): NavHostController?
    fun getNavHost(): NavHostController

    fun push(
        navigate: NavigateOption,
        middleWare: MiddleWare? = null
    )

    fun pop()
    fun popWithResult(key: String, bundle: Bundle)
    fun popAll(isPopInclusive: Boolean = false)
    fun refresh()
    fun popUntil(navigate: NavigateOption, isPopInclusive: Boolean = false)
    fun hasDestinationIdInStack(destinationId: Int): Boolean
    fun hasDeepLinkInStack(url: String): Boolean
    fun hasRouteInStack(route: String): Boolean
    fun getRootDestinationId(): Int

    @Composable
    fun <T> SetComposeListener(key: String, onResult: ((T?) -> Unit))
}

@SuppressLint("RestrictedApi")
class AppNavigatorImpl : AppNavigator, KoinComponent {
    private var navHost: NavHostController? = null
    private val middleWareManager: MiddleWareManager by inject()

    override fun setUpNavHost(navHost: NavHostController): NavHostController? {
        this.navHost = navHost
        return this.navHost
    }

    override fun getNavHost(): NavHostController = navHost!!

    override fun push(
        navigate: NavigateOption,
        middleWare: MiddleWare?
    ) {
        middleWareManager.checkRequiredFeature(middleWare, onSkipMiddleWare = {
            when (navigate) {
                is NavigateOption.Route -> {
                    navHost?.navigate(route = navigate.routeName, navigate.navOptions)
                }

                is NavigateOption.DeepLink -> {
                    navHost?.navigate(deepLink = Uri.parse(navigate.deeplink), navigate.navOptions)
                }
            }
        })
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

    @Composable
    override fun <T> SetComposeListener(key: String, onResult: (T?) -> Unit) {
        val lifecycle = LocalLifecycleOwner.current
        val navBackStackEntry by navHost!!.currentBackStackEntryAsState()
        val resultState =
            navBackStackEntry?.savedStateHandle?.getLiveData<T>(key = key)
        resultState?.observe(lifecycle) {
            onResult(it)
        }
        navBackStackEntry?.savedStateHandle?.remove<T>(key = key)
        resultState?.removeObservers(lifecycle)
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