package com.example.github.repository.search.user

import com.example.github.entity.common.PagingList
import com.example.github.entity.User
import com.example.github.entity.common.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun search(query: String, page: Int, perPage: Int): Flow<Result<PagingList<User>>>
}