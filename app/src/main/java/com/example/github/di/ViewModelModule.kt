package com.example.github.di

import com.example.github.ui.search.user.UserSearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
object ViewModelModule {
    fun module() = module {
        viewModel {
            UserSearchViewModel(get())
        }
    }
}