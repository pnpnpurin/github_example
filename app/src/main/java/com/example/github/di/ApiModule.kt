package com.example.github.di

import com.example.github.repository.search.user.ApiSearchUsersRepository
import com.example.github.repository.search.user.SearchUsersRepository
import org.koin.dsl.module

object ApiModule {
    fun module() = module {
        single<SearchUsersRepository> {
            ApiSearchUsersRepository(get())
        }
    }
}