package com.example.github.repository.user

import com.example.github.entity.Repo
import com.example.github.entity.common.Result
import com.example.github.entity.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
class MockUserRepository : UserRepository {

    override fun fetch(username: String): Flow<Result<User>> {
        return flow { emit(Result.Success(mockUserEntity())) }
    }

    override fun repos(username: String): Flow<Result<List<Repo>>> {
        return flow { emit(Result.Success(listOf<Repo>())) }
    }

    private fun mockUserEntity(): User {
        return User(1, "octocat", "https://github.com/images/error/octocat_happy.gif")
    }
}
