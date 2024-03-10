package com.dev.ipati.simplecomposenavigate.presentation

import androidx.lifecycle.ViewModel
import com.dev.ipati.simplecomposenavigate.presentation.bottombar.BottomNavItem
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    private val bottomMenu =
        listOf(BottomNavItem.Home, BottomNavItem.Search, BottomNavItem.Profile)

    val showBottomMenu = MutableStateFlow(bottomMenu)
    val shouldShowHide = MutableStateFlow(true)

    fun setShowHideBottomBar(isShow: Boolean) {
        shouldShowHide.value = isShow
    }
}