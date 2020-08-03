package com.example.github.di

import com.example.github.ui.search.user.UserSearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    fun module() = module {
        viewModel {
            UserSearchViewModel(get())
        }
    }
}