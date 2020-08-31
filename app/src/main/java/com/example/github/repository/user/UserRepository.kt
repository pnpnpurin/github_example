package com.example.github.repository.user

import com.example.github.entity.Repo
import com.example.github.entity.User
import com.example.github.entity.common.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun fetch(username: String): Flow<Result<User>>

    fun repos(username: String): Flow<Result<List<Repo>>>
}
