package com.dev.ipati.simplecomposenavigate.presentation.middleware

import com.dev.ipati.simplecomposenavigate.core.AppNavigator

interface MiddleWareManager {
    fun checkRequiredFeature(middleWare: MiddleWare?, onSkipMiddleWare: (() -> Unit))
}

class MiddleWareManagerImpl(
    private val appNavigator: AppNavigator
) : MiddleWareManager {
    override fun checkRequiredFeature(middleWare: MiddleWare?, onSkipMiddleWare: (() -> Unit)) {
        middleWare?.let {
            when (val result = middleWare.requiredFeature()) {
                is MiddleWareResult.Next -> {
                    appNavigator.push(result.navigate)
                }

                else -> {
                    onSkipMiddleWare()
                }
            }
        } ?: run {
            onSkipMiddleWare()
        }
    }
}