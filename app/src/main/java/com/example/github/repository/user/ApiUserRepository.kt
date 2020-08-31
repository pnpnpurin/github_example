package com.example.github.repository.user

import com.example.github.api.repo.RepoResponse
import com.example.github.api.user.UserApi
import com.example.github.api.user.UserResponse
import com.example.github.entity.Repo
import com.example.github.entity.common.Result
import com.example.github.entity.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
class ApiUserRepository(
    retrofit: Retrofit
) : UserRepository {

    private val api = retrofit.create(UserApi::class.java)

    override fun fetch(username: String): Flow<Result<User>> {
        return flow {
            runCatching {
                api.fetch(username)
            }.fold(
                onSuccess = { emit(Result.Success(it.toEntity())) },
                onFailure = { emit(Result.Error(it)) }
            )
        }
    }

    override fun repos(username: String): Flow<Result<List<Repo>>> {
        return flow {
            kotlin.runCatching {
                api.repos(username)
            }.fold(
                onSuccess = { emit(Result.Success(it.map { repo -> repo.toEntity() })) },
                onFailure = { emit(Result.Error(it)) }
            )
        }
    }

    private fun UserResponse.toEntity(): User {
        return User(id, login, imageUrl, bio, blog, followers, following)
    }

    private fun RepoResponse.toEntity(): Repo {
        return Repo(id, name, description, language, url, watchersCount, forksCount)
    }
}
