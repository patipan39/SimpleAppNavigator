package com.dev.ipati.simplecomposenavigate

import android.app.Application
import com.dev.ipati.simplecomposenavigate.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(mainModule))
        }
    }
}