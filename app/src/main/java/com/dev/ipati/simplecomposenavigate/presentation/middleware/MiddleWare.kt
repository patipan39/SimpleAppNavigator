package com.dev.ipati.simplecomposenavigate.presentation.middleware

import com.dev.ipati.simplecomposenavigate.core.NavigateOption

interface MiddleWare {
    fun requiredFeature(): MiddleWareResult
}

sealed class MiddleWareResult {
    data class Next(val navigate: NavigateOption) : MiddleWareResult()
    data object Resume : MiddleWareResult()
}