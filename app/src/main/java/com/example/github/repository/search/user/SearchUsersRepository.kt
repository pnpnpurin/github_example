package com.example.github.repository.search.user

import androidx.paging.PagingData
import com.example.github.entity.User
import kotlinx.coroutines.flow.Flow

interface SearchUsersRepository {
    fun fetch(query: String): Flow<PagingData<User>>
}
