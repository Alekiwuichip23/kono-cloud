package com.alessandra.konocloud

import android.app.Application
import com.alessandra.konocloud.di.AppContainer

class KonoApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
