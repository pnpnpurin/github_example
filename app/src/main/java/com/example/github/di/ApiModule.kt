package com.example.github.di

import com.example.github.repository.search.user.ApiUserRepository
import com.example.github.repository.search.user.UserRepository
import org.koin.dsl.module

object ApiModule {
    fun module() = module {
        single<UserRepository> {
            ApiUserRepository(get())
        }
    }
}