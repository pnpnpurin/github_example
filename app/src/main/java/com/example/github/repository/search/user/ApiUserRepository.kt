package com.example.github.repository.search.user

import com.example.github.api.search.SearchEnvelope
import com.example.github.api.search.user.UserApi
import com.example.github.api.search.user.UserResponse
import com.example.github.entity.common.PagingList
import com.example.github.entity.User
import com.example.github.entity.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit

class ApiUserRepository(retrofit: Retrofit) : UserRepository {

    private val api = retrofit.create(UserApi::class.java)

    override fun search(query: String, page: Int, perPage: Int): Flow<Result<PagingList<User>>> {
        return flow {
            runCatching {
                api.search(query, page, perPage)
            }.fold(
                onSuccess = { emit(Result.Success(it.toPagingList(page, perPage))) },
                onFailure = { emit(Result.Error(it)) }
            )
        }
    }

    private fun SearchEnvelope<UserResponse>.toPagingList(page: Int, perPage: Int): PagingList<User> {
        val entities = items.map { it.toEntity() }
        return PagingList(
            totalCount,
            page,
            perPage,
            entities
        )
    }

    private fun UserResponse.toEntity(): User {
        return User(id, login, imageUrl)
    }
}