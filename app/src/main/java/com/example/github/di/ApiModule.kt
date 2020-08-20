package com.example.github.di

import com.example.github.repository.search.user.ApiSearchUsersRepository
import com.example.github.repository.search.user.SearchUsersRepository
import com.example.github.repository.user.ApiUserRepository
import com.example.github.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module

@ExperimentalCoroutinesApi
object ApiModule {
    fun module() = module {
        single<SearchUsersRepository> {
            ApiSearchUsersRepository(get())
        }

        single<UserRepository> {
            ApiUserRepository(get())
        }
    }
}
