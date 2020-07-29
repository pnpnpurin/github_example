package com.example.github

import android.app.Application
import com.example.github.di.ApiModule
import com.example.github.di.NetworkModule
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GitHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    NetworkModule.module(),
                    ApiModule.module()
                )
            )
        }
        Logger.addLogAdapter(AndroidLogAdapter())
    }
}
