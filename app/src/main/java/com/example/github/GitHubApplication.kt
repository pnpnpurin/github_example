package com.example.github

import android.app.Application
import com.example.github.di.ApiModule
import com.example.github.di.NetworkModule
import com.example.github.di.ViewModelModule
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalCoroutinesApi
class GitHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()




        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    NetworkModule.module(),
                    ApiModule.module(),
                    ViewModelModule.module()
                )
            )
        }
        Logger.addLogAdapter(AndroidLogAdapter())
    }
}
