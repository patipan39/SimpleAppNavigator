package com.dev.ipati.simplecomposenavigate.di

import com.dev.ipati.simplecomposenavigate.core.AppNavigator
import com.dev.ipati.simplecomposenavigate.core.AppNavigatorImpl
import com.dev.ipati.simplecomposenavigate.presentation.MainViewModel
import com.dev.ipati.simplecomposenavigate.presentation.middleware.MiddleWareManager
import com.dev.ipati.simplecomposenavigate.presentation.middleware.MiddleWareManagerImpl
import com.dev.ipati.simplecomposenavigate.presentation.profile.ProfileMiddleWare
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val mainModule = module {
    singleOf(::AppNavigatorImpl) { bind<AppNavigator>() }

    viewModel { MainViewModel() }

    factoryOf(::MiddleWareManagerImpl) { bind<MiddleWareManager>() }

    factory { ProfileMiddleWare() }
}