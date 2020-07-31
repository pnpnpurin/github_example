package com.example.github.di

import com.example.github.BuildConfig
import com.example.github.api.common.ApiInterceptions
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val TIMEOUT_INTERVAL_SECOND = 15L

    fun module() = module {
        single {
            OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                    }
                    connectTimeout(TIMEOUT_INTERVAL_SECOND, TimeUnit.SECONDS)
                    readTimeout(TIMEOUT_INTERVAL_SECOND, TimeUnit.SECONDS)

                    addInterceptor(ApiInterceptions::interceptAdditionalHeader)
                    addInterceptor(ApiInterceptions::interceptServerException)
                    addInterceptor(ApiInterceptions::interceptClientException)
                    addInterceptor(ApiInterceptions::interceptNetworkingException)
                }
                .build()
        }

        single {
            val contentType = "application/json".toMediaType()
            Json(
                JsonConfiguration.Stable.copy(
                    ignoreUnknownKeys = true,
                    isLenient = true,
                    serializeSpecialFloatingPointValues = true,
                    useArrayPolymorphism = true
                )
            ).asConverterFactory(contentType)
        }

        single {
            provideRetrofit(get(), get())
        }
    }

    private fun provideRetrofit(okHttpClient: OkHttpClient, factory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(factory)
            .client(okHttpClient)
            .build()
    }
}
