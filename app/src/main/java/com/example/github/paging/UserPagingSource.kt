package com.example.github.paging

import androidx.paging.PagingSource
import com.example.github.api.common.ApiConfig
import com.example.github.api.common.NoResultException
import com.example.github.api.search.user.SearchUsersApi
import com.example.github.api.user.UserResponse
import com.example.github.entity.User
import kotlin.math.ceil

class UserPagingSource(
    private val api: SearchUsersApi,
    private val query: String
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val position = params.key ?: ApiConfig.STARTING_PAGE_INDEX
        var maxPosition = ApiConfig.STARTING_PAGE_INDEX
        return runCatching {
            val response = api.search(query, position, params.loadSize)
            val total = response.totalCount
            if (total == 0) throw NoResultException()
            maxPosition = if (total <= params.loadSize) 1 else total / params.loadSize + ceil(total % params.loadSize / 10.0).toInt()
            response.items.map { it.toEntity() }
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = if (position == ApiConfig.STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (maxPosition == position) null else position + 1
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    private fun UserResponse.toEntity(): User {
        return User(id, login, imageUrl, bio, blog, followers, following)
    }
}
